package top.huzz.resilix.core;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import top.huzz.resilix.idempotent.IdempotentJudge;
import top.huzz.resilix.idempotent.IdempotentKey;
import top.huzz.resilix.idempotent.SkippedIdempotentJudge;
import top.huzz.resilix.predicate.HandlerRunPredicate;
import top.huzz.resilix.recorder.NopePhaseRecorder;
import top.huzz.resilix.recorder.PhaseRecorder;
import top.huzz.resilix.additional.AdditionalContextAction;
import top.huzz.resilix.additional.AwareCacheAdditionalContextAction;
import top.huzz.resilix.cache.AwareCache;
import top.huzz.resilix.callback.NopePhaseCallback;
import top.huzz.resilix.callback.PhaseCallback;
import top.huzz.resilix.exception.IdempotentJudgeException;
import top.huzz.resilix.exception.PhaseStoppedException;
import top.huzz.resilix.exception.RemoteLaunchFailedException;
import top.huzz.resilix.handler.RestApiTriggerRunHandler;
import top.huzz.resilix.handler.RunHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 处理器管理器基础类，用于管理所有的处理器，按照阶段顺序执行
 *
 * @author chenji
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractRunHandlerManager implements RunHandlerManager {
    /**
     * 存储所有的处理器，按照阶段进行存储
     */
    protected final Map<Phase, RunHandler<RunContext>> handlerMap;
    /**
     * 第一个计划阶段，通常是处理器中阶段最小的那个阶段
     */
    protected final Phase firstPlanPhase;
    /**
     * 异步阶段调用默认的线程池
     */
    protected final ExecutorService poolExecutor;
    /**
     * 异步阶段回调器，用于触发异步阶段
     */
    protected final PhaseCallback asyncPhaseCallback;
    /**
     * 阶段停止状态检查器，用于检查当前阶段是否已经停止
     */
    protected final PhaseStopStatusChecker phaseStopStatusChecker;
    /**
     * 返回额外的上下文操作
     */
    protected final List<AdditionalContextAction<RunContext>> additionalContextActions;
    /**
     * 基于环境的感知缓存，存储在环境感知缓存中
     */
    protected final Map<AwareCache.Type, AwareCache> envAwareCacheMap;
    /**
     * 默认的阶段记录器
     */
    protected final PhaseRecorder<? extends RunContext> phaseRecorder;
    /**
     * 全局的幂等判断器
     */
    protected final IdempotentJudge globalIdempotentJudge;
    /**
     * 手动注册的幂等判断器
     */
    protected final Map<Phase, IdempotentJudge> idempotentJudgeMap = new HashMap<>();
    /**
     * 整个执行链路完成后，不管最后的结果是成功还是失败，一定会执行的逻辑
     */
    protected final List<Consumer<RunContext>> finallyConsumer = new ArrayList<>();
    /**
     * 上下文类的类型
     */
    @Getter
    private final Class<? extends RunContext> cxtClass;

    AbstractRunHandlerManager(@Nonnull List<RunHandler<RunContext>> handlers,
                              @Nullable ExecutorService poolExecutor,
                              @Nullable PhaseCallback asyncPhaseCallback,
                              @Nullable PhaseStopStatusChecker phaseStopStatusChecker,
                              @Nullable List<AdditionalContextAction<RunContext>> additionalContextActions,
                              @Nullable Map<AwareCache.Type, AwareCache> envAwareCacheMap,
                              @Nullable PhaseRecorder<? extends RunContext> phaseRecorder,
                              @Nullable IdempotentJudge globalIdempotentJudge
    ) {
        checkHandler(handlers);
        handlerMap = handlers.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(RunHandler::phase, Function.identity()),
                        Collections::unmodifiableMap));
        firstPlanPhase = handlerMap.keySet().stream().min(Comparator.comparingInt(Phase::ordinal)).orElse(null);
        precessFinallyConsumer(handlers);

        this.poolExecutor = poolExecutor == null ? Executors.newSingleThreadExecutor() : poolExecutor;
        this.asyncPhaseCallback = asyncPhaseCallback == null ? new NopePhaseCallback() : asyncPhaseCallback;
        this.phaseStopStatusChecker = phaseStopStatusChecker == null ? new NopePhaseStopStatusChecker() : phaseStopStatusChecker;
        this.additionalContextActions = additionalContextActions == null ? Collections.emptyList() : additionalContextActions;
        this.envAwareCacheMap = envAwareCacheMap == null ? Collections.emptyMap() : envAwareCacheMap;
        this.phaseRecorder = phaseRecorder == null ? new NopePhaseRecorder() : phaseRecorder;
        this.globalIdempotentJudge = globalIdempotentJudge;
        this.cxtClass = checkContextClass(handlers.get(0));
    }


    @Override
    public void start(RunContext context) throws NullPointerException, IllegalArgumentException {
        if (context == null) {
            throw new NullPointerException("RunContext cannot be null");
        }
        if (!context.getClass().equals(cxtClass)) {
            throw new IllegalArgumentException("RunContext type mismatch, expected: " + cxtClass.getName() + ", but found: " + context.getClass().getName());
        }
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


    private void next(RunContext context, @Nullable Phase phase) {
        if (phase == null) {
            RunContext.removeCurrentCtx();
            return;
        }
        if (phase.isDeprecated()) {
            next(context, phase.next());
            return;
        }
        RunHandler<RunContext> handler = handlerMap.get(phase);
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
        List<HandlerRunPredicate<RunContext>> runPredicate = handler.runPredicate();
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
            ExecutorService poolExecutor = Objects.requireNonNullElse(phase.customExecutor(), this.poolExecutor);
            poolExecutor.execute(() -> {
                // 异步阶段，需要重新设置上下文，因为ThreadLocal是线程隔离的
                RunContext.setCurrentCtx(duplicate);
                execute(duplicate, handler);
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
    private void execute(RunContext context, RunHandler<RunContext> handler) {
        PhaseRecorder<RunContext> recorder = handler.getRecorder();
        if (recorder == null) {
            recorder = (PhaseRecorder<RunContext>) phaseRecorder;
        }

        // 当前阶段快照，如果是"远程调用触发下一个阶段"这种阶段的运行，可能会修改掉上下文，所以需要使用快照，给Recorder使用
        RunContext currentPhaseSnapshot = handler instanceof RestApiTriggerRunHandler ? context.duplicate() : context;

        try {
            phaseStopStatusChecker.check(context);

            // 阶段开始，记录一些需要存储的数据
            logReadyFor(context, recorder);

            handler.handle(context);
            logEnd(currentPhaseSnapshot, recorder, null);

            // 执行成功，发送事件
            Object extraInfo = null;
            if (handler instanceof ExtraInfoProvider provider) {
                extraInfo = provider.getExtraInfo(context);
            }
            asyncPhaseCallback.callback(context, extraInfo);
        } catch (Exception e) {
            failed(context, currentPhaseSnapshot, e);
            if (e instanceof RemoteLaunchFailedException) {
                // 如果是拉起远程失败，这种失败指的是，请求都没有发送出去，需要把原始context也设置一下失败标志
                failed(context, currentPhaseSnapshot, (Exception) e.getCause());
            }
            if (e instanceof PhaseStoppedException) {
                context.setStopped(true);
                currentPhaseSnapshot.setStopped(true);
            }

            logEnd(currentPhaseSnapshot, recorder, e);
        } finally {
            handler.postHandle(currentPhaseSnapshot);
        }
    }

    private void failed(RunContext context, RunContext currentPhaseSnapshot, Exception e) {
        context.setSuccess(false);
        context.setException(e);
        currentPhaseSnapshot.setSuccess(false);
        currentPhaseSnapshot.setException(e);
    }


    protected void logReadyFor(RunContext context, PhaseRecorder<RunContext> recorder) {
        try {
            recorder.readyFor(context);
        } catch (Exception e) {
            log.error("记录阶段开始异常", e);
        }
    }

    protected void logEnd(RunContext context, PhaseRecorder<RunContext> recorder, Exception e) {
        try {
            recorder.end(context, e);
        } catch (Exception end) {
            log.error("记录阶段结束异常", end);
        }
    }

    private void precessFinallyConsumer(List<? extends RunHandler<RunContext>> handlers) {
        for (RunHandler<RunContext> handler : handlers) {
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
    protected boolean executeIdempotentJudge(RunContext context, Phase phase) throws IdempotentJudgeException {
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
            result = executeIdempotentJudge(context, globalIdempotentJudge, key, phase);
            if (result != null) {
                return result;
            }
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
    private Boolean executeIdempotentJudge(RunContext context, IdempotentJudge idempotentJudge, IdempotentKey key, Phase phase) throws IdempotentJudgeException {
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
    private void safeDoAdditionalAction(RunContext context) {
        try {
            for (AdditionalContextAction<RunContext> additionalContextAction : additionalContextActions) {
                if (context instanceof AwareCacheRunContext awareCacheRunContext) {
                    awareCacheRunContext.trySetUpEnvAwareCacheMap(envAwareCacheMap);
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
    @Override
    public RunHandlerManager addIdempotentJudge(Phase phase, IdempotentJudge idempotentJudge) {
        idempotentJudgeMap.put(phase, idempotentJudge);
        return this;
    }

    protected void checkHandler(List<RunHandler<RunContext>> handlers) {
        if (CollectionUtils.isEmpty(handlers)) {
            throw new IllegalArgumentException("处理器列表不能为空");
        }

        List<String> phaseTypeNames = handlers.stream().map(RunHandler::phase).map(Object::getClass).map(Class::getSimpleName).distinct().toList();
        if (phaseTypeNames.size() > 1) {
            throw new IllegalArgumentException("所有的处理器必须是同一种类型的，这一批次的类型：" + String.join(", ", phaseTypeNames));
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends RunContext> checkContextClass(RunHandler<RunContext> handler) {
        Class<?> handlerClass = handler.getClass();
        while (handlerClass != null) {
            Type genericSuperclass = handlerClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType parameterizedType) {
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?> genericType) {
                    if (RunContext.class.isAssignableFrom(genericType)) {
                        return (Class<? extends RunContext>) genericType;
                    } else {
                        throw new IllegalArgumentException("RunHandler must be generic type of RunContext, but found: " + genericType.getName());
                    }
                }
            }
            handlerClass = handlerClass.getSuperclass();
        }
        throw new IllegalArgumentException("Cannot resolve RunContext type from handler: " + handler.getClass().getName());
    }
}

