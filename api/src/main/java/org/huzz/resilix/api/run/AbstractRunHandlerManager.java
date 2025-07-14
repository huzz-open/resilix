package org.huzz.resilix.api.run;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.huzz.resilix.api.idempotent.IdempotentJudge;
import org.huzz.resilix.api.idempotent.IdempotentKey;
import org.huzz.resilix.api.idempotent.SkippedIdempotentJudge;
import org.huzz.resilix.api.predicate.HandlerRunPredicate;
import org.huzz.resilix.api.recorder.NopePhaseRecorder;
import org.huzz.resilix.api.recorder.PhaseRecorder;
import org.huzz.resilix.api.run.additional.AdditionalContextAction;
import org.huzz.resilix.api.run.additional.AwareCacheAdditionalContextAction;
import org.huzz.resilix.api.run.cache.AwareCache;
import org.huzz.resilix.api.run.callback.NopePhaseCallback;
import org.huzz.resilix.api.run.callback.PhaseCallback;
import org.huzz.resilix.api.run.exception.IdempotentJudgeException;
import org.huzz.resilix.api.run.exception.PhaseStoppedException;
import org.huzz.resilix.api.run.exception.RemoteLaunchFailedException;
import org.huzz.resilix.api.run.handler.RestApiTriggerRunHandler;
import org.huzz.resilix.api.run.handler.RunHandler;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 处理器管理器基础类，用于管理所有的处理器，按照阶段顺序执行
 *
 * @param <C> 上下文类型
 * @author chenji
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractRunHandlerManager<C extends RunContext> {

    /**
     * @return 返回异步阶段调用默认的线程池
     */
    @Nonnull
    protected abstract ExecutorService poolExecutor();

    /**
     * @return 返回异步阶段回调器
     */
    @Nonnull
    protected PhaseCallback asyncPhaseCallback() {
        return new NopePhaseCallback();
    }

    /**
     * @return 返回阶段状态检查器
     */
    @Nonnull
    protected PhaseStopStatusChecker phaseStatusChecker() {
        return new NopePhaseStopStatusChecker();
    }

    /**
     * @return 返回额外的上下文操作
     */
    @Nonnull
    protected List<AdditionalContextAction<C>> additionalContextAction() {
        return ImmutableList.of();
    }

    /**
     * @return 返回环境感知缓存
     */
    @Nonnull
    protected Map<AwareCache.Type, AwareCache> envAwareCacheMap() {
        return ImmutableMap.of();
    }

    /**
     * @return 返回全局的幂等判断器
     */
    @Nullable
    protected IdempotentJudge globalIdempotentJudge() {
        return null;
    }

    protected final Map<Phase, RunHandler<C>> handlerMap;
    protected final Phase firstPlanPhase;
    // 存放手动注册的幂等判断器
    protected final Map<Phase, IdempotentJudge> idempotentJudgeMap = new HashMap<>();

    protected final PhaseRecorder<?> nopePhaseRecorder = new NopePhaseRecorder();

    /**
     * 整个执行链路完成后，不管最后的结果是成功还是失败，一定会执行的逻辑
     */
    protected final List<Consumer<C>> finallyConsumer = new ArrayList<>();

    protected AbstractRunHandlerManager(List<? extends RunHandler<C>> handlers) {
        handlerMap = handlers.stream().collect(Collectors.toMap(RunHandler::phase, Function.identity()));
        // 获取到第一个阶段
        firstPlanPhase = handlerMap.keySet().stream().min(Comparator.comparingInt(Phase::ordinal)).orElse(null);

        // 设置finallyConsumer
        precessFinallyConsumer(handlers);
    }

    public void start(C context) {
        try {
            RunContext.setCurrentCtx(context);
            // 可以支持从任意阶段开始执行
            Phase start = context.getCurrentPhase() != null ? context.getCurrentPhase() : firstPlanPhase;

            safeDoAdditionalAction(context);

            next(context, start);
        } catch (IdempotentJudgeException e) {
            log.warn("幂等判断结果为true，跳过接下来的所有阶段执行，key：{}", e.getKey().idempotentKey());
            context.setSkipped(true);
        } finally {
            try {
                finallyConsumer.forEach(fc -> fc.accept(context));
            } catch (Exception e) {
                log.error("执行finallyConsumer异常", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void next(C context, @Nullable Phase phase) {
        if (phase == null) {
            RunContext.removeCurrentCtx();
            return;
        }
        if (phase.isDeprecated()) {
            next(context, phase.next());
            return;
        }
        RunHandler<C> handler = handlerMap.get(phase);
        if (handler == null) {
            // 如果没有找到对应的处理器，说明在本地没有找到对应的处理器，可能是远程调用触发下一个阶段，这里直接结束
            return;
        }

        // 设置当前阶段
        context.setCurrentPhase(phase);
        // 默认成功
        context.setSuccess(true);
        context.setException(null);

        // 跳过判断
        List<HandlerRunPredicate<C>> runPredicate = handler.runPredicate();
        try {
            if (runPredicate.stream().anyMatch(predicate -> !predicate.shouldRun(context))) {
                next(context, context.getCurrentPhase().next());
                return;
            }
        } catch (Exception e) {
            log.error("执行判断逻辑异常", e);
            throw new RuntimeException("执行判断逻辑异常", e);
        }

        // 幂等判断
        if (executeIdempotentJudge(context, phase)) {
            return;
        }

        // 使用上下文副本的场景
        // 1、异步执行阶段：防止在异步阶段，原先的context被其他线程修改，然后出现不可预知的问题，所以使用副本
        // 2、远程调用触发下一个阶段：在这种阶段中，会远程触发下一个阶段的运行，而被触发的阶段可能会修改上下文，所以需要使用副本
        if (phase.isAsync()) {
            // 异步执行
            RunContext duplicate = context.duplicate();
            ExecutorService poolExecutor = Objects.requireNonNullElseGet(phase.customExecutor(), this::poolExecutor);
            poolExecutor.execute(() -> {
                // 异步阶段，需要重新设置上下文，因为ThreadLocal是线程隔离的
                RunContext.setCurrentCtx(duplicate);
                execute((C) duplicate, handler);
            });
            // 因为handler可能是远程服务提供的，因此这里需要使用Context里面的Phase，而不是handler里面的Phase，下同
            next(context, context.getCurrentPhase().next());
            return;
        }

        // 同步执行阶段
        execute(context, handler);
        if (context.isSuccess()) {
            next(context, context.getCurrentPhase().next());
        }
    }

    @SuppressWarnings("unchecked")
    protected void execute(C context, RunHandler<C> handler) {
        PhaseRecorder<C> recorder = handler.getRecorder();
        if (recorder == null) {
            recorder = (PhaseRecorder<C>) nopePhaseRecorder;
        }

        // 当前阶段快照，如果是"远程调用触发下一个阶段"这种阶段的运行，可能会修改掉上下文，所以需要使用快照，给Recorder使用
        RunContext currentPhaseSnapshot = handler instanceof RestApiTriggerRunHandler ? context.duplicate() : context;

        try {
            PhaseStopStatusChecker statusChecker = phaseStatusChecker();
            statusChecker.check(context);

            // 阶段开始，记录一些需要存储的数据
            logReadyFor(context, recorder);

            handler.handle(context);
            logEnd((C) currentPhaseSnapshot, recorder, null);

            // 执行成功，发送事件
            Object extraInfo = null;
            if (handler instanceof ExtraInfoProvider provider) {
                extraInfo = provider.getExtraInfo(context);
            }
            PhaseCallback phaseCallback = asyncPhaseCallback();
            phaseCallback.callback(context, extraInfo);
        } catch (Exception e) {
            failed(context, (C) currentPhaseSnapshot, e);
            if (e instanceof RemoteLaunchFailedException) {
                // 如果是拉起远程失败，这种失败指的是，请求都没有发送出去，需要把原始context也设置一下失败标志
                failed(context, (C) currentPhaseSnapshot, (Exception) e.getCause());
            }
            if (e instanceof PhaseStoppedException) {
                context.setStopped(true);
                currentPhaseSnapshot.setStopped(true);
            }

            logEnd((C) currentPhaseSnapshot, recorder, e);
        } finally {
            handler.postHandle((C) currentPhaseSnapshot);
        }
    }

    protected void failed(C context, C currentPhaseSnapshot, Exception e) {
        context.setSuccess(false);
        context.setException(e);
        currentPhaseSnapshot.setSuccess(false);
        currentPhaseSnapshot.setException(e);
    }


    protected void logReadyFor(C context, PhaseRecorder<C> recorder) {
        try {
            recorder.readyFor(context);
        } catch (Exception e) {
            log.error("记录阶段开始异常", e);
        }
    }

    protected void logEnd(C context, PhaseRecorder<C> recorder, Exception e) {
        try {
            recorder.end(context, e);
        } catch (Exception end) {
            log.error("记录阶段结束异常", end);
        }
    }

    private void precessFinallyConsumer(List<? extends RunHandler<C>> handlers) {
        for (RunHandler<C> handler : handlers) {
            finallyConsumer.add(handler::finallyHandle);
        }
    }

    /**
     * 执行幂等判断，分别有三种类型，注册的幂等判断器、单个阶段的幂等判断器、全局的幂等判断器，优先级依次递减，如果有一个返回true，就跳过执行
     * <p/>这里实际上可以将幂等判断放在跳过判断器里面去实现，但这样一来就没有办法把这个特性抽出来，因此还是单独将幂等判断单独抽出一个特性实现
     *
     * @param context 上下文
     * @param phase   阶段
     * @return true：幂等判断的结果是要跳过执行，false：不跳过执行，要接着执行接下来的阶段
     * @throws IdempotentJudgeException 幂等判断异常，表示当前阶段下面的所有阶段都不执行了
     * @see HandlerRunPredicate
     */
    protected boolean executeIdempotentJudge(C context, Phase phase) throws IdempotentJudgeException {
        if (context instanceof IdempotentKey key) {
            // 使用addIdempotentJudge方法注册的幂等判断器优先级最高
            IdempotentJudge registryIdempotentJudge = idempotentJudgeMap.get(phase);
            Boolean result = executeIdempotentJudge(context, registryIdempotentJudge, key, phase);
            if (result != null) {
                return result;
            }

            // 单个阶段的幂等判断器优先级次之
            IdempotentJudge phaseIdempotentJudge = phase.idempotentJudge();
            result = executeIdempotentJudge(context, phaseIdempotentJudge, key, phase);
            if (result != null) {
                return result;
            }

            // 全局的幂等判断器优先级最低
            IdempotentJudge globalIdempotentJudge = globalIdempotentJudge();
            result = executeIdempotentJudge(context, globalIdempotentJudge, key, phase);
            if (result != null) {
                return result;
            }
        } else {
            log.info("当前无需进行幂等判断，阶段：{}", phase);
        }
        return false;
    }

    /**
     * 执行幂等判断
     *
     * @param context         上下文
     * @param idempotentJudge 幂等判断器
     * @param key             幂等判断key
     * @param phase           阶段
     * @return null：没有执行幂等判断，true：跳过执行，false：不跳过执行
     * @throws IdempotentJudgeException 幂等判断异常，表示当前阶段下面的所有阶段都不执行了
     */
    private Boolean executeIdempotentJudge(C context, IdempotentJudge idempotentJudge, IdempotentKey key, Phase phase) throws IdempotentJudgeException {
        if (idempotentJudge == null) {
            // 返回null表示没有执行幂等判断，方便后续使用其他的幂等判断器
            return null;
        }
        String idempotentJudgeClass = idempotentJudge.getClass().getName();
        log.info("幂等判断器[{}]存在，key：{}", idempotentJudgeClass, key.idempotentKey());
        if (idempotentJudge.judge(key)) {
            log.info("幂等判断器[{}]返回结果为true，表示任务已经执行过，跳过阶段{}的执行，key：{}", idempotentJudgeClass, phase, key.idempotentKey());
            if (key instanceof SkippedIdempotentJudge) {
                throw new IdempotentJudgeException(key);
            }

            next(context, context.getCurrentPhase().next());
            return true;
        } else {
            log.info("幂等判断器[{}]返回结果为false，表示任务未执行，准备执行阶段{}，key：{}", idempotentJudgeClass, phase, key.idempotentKey());
            idempotentJudge.put(key);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public void safeDoAdditionalAction(C context) {
        try {
            List<AdditionalContextAction<C>> additionalContextActions = additionalContextAction();
            for (AdditionalContextAction<C> additionalContextAction : additionalContextActions) {
                if (context instanceof AwareCacheRunContext awareCacheRunContext) {
                    awareCacheRunContext.trySetUpEnvAwareCacheMap(envAwareCacheMap());
                    if (additionalContextAction instanceof AwareCacheAdditionalContextAction<?> awareCacheContextAction) {
                        ((AwareCacheAdditionalContextAction<AwareCacheRunContext>) awareCacheContextAction).action(awareCacheRunContext);
                    } else {
                        additionalContextAction.action(context);
                    }
                } else {
                    additionalContextAction.action(context);
                }
            }
        } catch (Exception e) {
            log.warn("执行额外操作异常", e);
        }
    }

    /**
     * 手动注册幂等判断器
     *
     * @param phase           阶段
     * @param idempotentJudge 幂等判断器
     * @return 当前上下文管理器
     */
    public AbstractRunHandlerManager<C> addIdempotentJudge(Phase phase, IdempotentJudge idempotentJudge) {
        idempotentJudgeMap.put(phase, idempotentJudge);
        return this;
    }
}

