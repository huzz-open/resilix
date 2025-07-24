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
 * Base class for handler manager, manages all handlers and executes them in phase order
 *
 * @author chenji
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractRunHandlerManager implements RunHandlerManager {
    /**
     * Stores all handlers, organized by phase
     */
    protected final Map<Phase, RunHandler<RunContext>> handlerMap;
    /**
     * The first plan phase, usually the minimum phase among all handlers
     */
    protected final Phase firstPlanPhase;
    /**
     * Default thread pool for asynchronous phase calls
     */
    protected final ExecutorService poolExecutor;
    /**
     * Asynchronous phase callback for triggering async phases
     */
    protected final PhaseCallback asyncPhaseCallback;
    /**
     * Phase stop status checker for checking if the current phase has stopped
     */
    protected final PhaseStopStatusChecker phaseStopStatusChecker;
    /**
     * Returns additional context actions
     */
    protected final List<AdditionalContextAction<RunContext>> additionalContextActions;
    /**
     * Environment-aware cache map, stored in environment-aware cache
     */
    protected final Map<AwareCache.Type, AwareCache> envAwareCacheMap;
    /**
     * Default phase recorder
     */
    protected final PhaseRecorder<? extends RunContext> phaseRecorder;
    /**
     * Global idempotent judge
     */
    protected final IdempotentJudge globalIdempotentJudge;
    /**
     * Manually registered idempotent judges
     */
    protected final Map<Phase, IdempotentJudge> idempotentJudgeMap = new HashMap<>();
    /**
     * Logic that will definitely be executed after the entire execution chain is completed,
     * regardless of whether the final result is success or failure
     */
    protected final List<Consumer<RunContext>> finallyConsumer = new ArrayList<>();
    /**
     * Type of the context class
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
            // Support starting execution from any phase
            Phase start = context.getCurrentPhase() != null ? context.getCurrentPhase() : firstPlanPhase;

            safeDoAdditionalAction(context);

            next(context, start);
        } catch (IdempotentJudgeException e) {
            log.warn("Idempotent judgment result is true, skipping execution of all subsequent phases, key: {}", e.getKey().idempotentKey());
            context.setSkipped(true);
        } finally {
            try {
                finallyConsumer.forEach(fc -> fc.accept(context));
            } catch (Exception e) {
                log.error("Exception occurred while executing finallyConsumer", e);
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
            // If no corresponding handler is found, it means no handler is found locally, 
            // possibly a remote call triggering the next phase, so end here directly
            return;
        }

        // Set current phase
        context.setCurrentPhase(phase);
        // Default to success
        context.setSuccess(true);
        context.setException(null);

        // Skip judgment
        List<HandlerRunPredicate<RunContext>> runPredicate = handler.runPredicate();
        try {
            if (runPredicate.stream().anyMatch(predicate -> !predicate.shouldRun(context))) {
                next(context, context.getCurrentPhase().next());
                return;
            }
        } catch (Exception e) {
            log.error("Exception occurred while executing judgment logic", e);
            throw new RuntimeException("Exception occurred while executing judgment logic", e);
        }

        // Idempotent judgment
        if (executeIdempotentJudge(context, phase)) {
            return;
        }

        // Scenarios for using context copies
        // 1. Asynchronous execution phase: To prevent the original context from being modified by other threads during asynchronous phase, causing unpredictable issues, so use a copy
        // 2. Remote call triggering next phase: In this phase, it will remotely trigger the execution of the next phase, and the triggered phase may modify the context, so a copy is needed
        if (phase.isAsync()) {
            // Asynchronous execution
            RunContext duplicate = context.duplicate();
            ExecutorService poolExecutor = Objects.requireNonNullElse(phase.customExecutor(), this.poolExecutor);
            poolExecutor.execute(() -> {
                // Asynchronous phase requires resetting context because ThreadLocal is thread-isolated
                RunContext.setCurrentCtx(duplicate);
                execute(duplicate, handler);
            });
                            // Because handler may be provided by remote service, we need to use Phase from Context instead of Phase from handler, same below
            next(context, context.getCurrentPhase().next());
            return;
        }

        // Synchronous execution phase
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

        // Current phase snapshot, if it's "remote call triggering next phase" type execution, it may modify the context, so snapshot is needed for Recorder
        RunContext currentPhaseSnapshot = handler instanceof RestApiTriggerRunHandler ? context.duplicate() : context;

        try {
            phaseStopStatusChecker.check(context);

            // Phase start, record some data that needs to be stored
            logReadyFor(context, recorder);

            handler.handle(context);
            logEnd(currentPhaseSnapshot, recorder, null);

            // Execution successful, send event
            Object extraInfo = null;
            if (handler instanceof ExtraInfoProvider provider) {
                extraInfo = provider.apply(context);
            }
            asyncPhaseCallback.callback(context, extraInfo);
        } catch (Exception e) {
            failed(context, currentPhaseSnapshot, e);
            if (e instanceof RemoteLaunchFailedException) {
                // If it's a remote launch failure, this type of failure means the request wasn't even sent out, need to set failure flag on original context as well
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
            log.error("Exception occurred while recording phase start", e);
        }
    }

    protected void logEnd(RunContext context, PhaseRecorder<RunContext> recorder, Exception e) {
        try {
            recorder.end(context, e);
        } catch (Exception end) {
            log.error("Exception occurred while recording phase end", end);
        }
    }

    private void precessFinallyConsumer(List<? extends RunHandler<RunContext>> handlers) {
        for (RunHandler<RunContext> handler : handlers) {
            finallyConsumer.add(handler::finallyHandle);
        }
    }

    /**
     * Execute idempotent judgment, there are three types, registered idempotent judges,
     * single-phase idempotent judges, and global idempotent judges, with decreasing priority.
     * If one returns true, execution is skipped.
     * <p/>Here, idempotent judgment can actually be implemented in the skip judgment,
     * but this way, this feature cannot be extracted. Therefore, it is still implemented
     * separately as a separate feature.
     *
     * @param context  context
     * @param phase    phase
     * @return true: The result of the idempotent judgment is to skip execution, false: Do not skip execution, continue to execute the next phase
     * @throws IdempotentJudgeException If idempotent judgment fails, it means that all phases below the current phase will not be executed
     * @see HandlerRunPredicate
     */
    protected boolean executeIdempotentJudge(RunContext context, Phase phase) throws IdempotentJudgeException {
        if (context instanceof IdempotentKey key) {
            // Registered idempotent judges have the highest priority
            IdempotentJudge registryIdempotentJudge = idempotentJudgeMap.get(phase);
            Boolean result = executeIdempotentJudge(context, registryIdempotentJudge, key, phase);
            if (result != null) {
                return result;
            }

            // Single-phase idempotent judges have the next priority
            IdempotentJudge phaseIdempotentJudge = phase.idempotentJudge();
            result = executeIdempotentJudge(context, phaseIdempotentJudge, key, phase);
            if (result != null) {
                return result;
            }

            // Global idempotent judges have the lowest priority
            result = executeIdempotentJudge(context, globalIdempotentJudge, key, phase);
            if (result != null) {
                return result;
            }
        }
        return false;
    }

    /**
     * Execute idempotent judgment
     *
     * @param context          context
     * @param idempotentJudge  idempotent judge
     * @param key              idempotent key
     * @param phase            phase
     * @return null: No idempotent judgment executed, true: Skip execution, false: Do not skip execution
     * @throws IdempotentJudgeException If idempotent judgment fails, it means that all phases below the current phase will not be executed
     */
    private Boolean executeIdempotentJudge(RunContext context, IdempotentJudge idempotentJudge, IdempotentKey key, Phase phase) throws IdempotentJudgeException {
        if (idempotentJudge == null) {
            // Return null indicates that no idempotent judgment was executed, making it easier to use other idempotent judges
            return null;
        }
        String idempotentJudgeClass = idempotentJudge.getClass().getName();
        log.info("Idempotent judge [{}] exists, key: {}", idempotentJudgeClass, key.idempotentKey());
        if (idempotentJudge.judge(key)) {
            log.info("Idempotent judge [{}] returned true, indicating that the task has been executed, skipping phase {} execution, key: {}", idempotentJudgeClass, phase, key.idempotentKey());
            if (key instanceof SkippedIdempotentJudge) {
                throw new IdempotentJudgeException(key);
            }

            next(context, context.getCurrentPhase().next());
            return true;
        } else {
            log.info("Idempotent judge [{}] returned false, indicating that the task has not been executed, preparing to execute phase {}, key: {}", idempotentJudgeClass, phase, key.idempotentKey());
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
            log.warn("Exception occurred while executing additional operations", e);
        }
    }

    /**
     * Manually register idempotent judge
     *
     * @param phase            phase
     * @param idempotentJudge  idempotent judge
     * @return Current context manager
     */
    @Override
    public RunHandlerManager addIdempotentJudge(Phase phase, IdempotentJudge idempotentJudge) {
        idempotentJudgeMap.put(phase, idempotentJudge);
        return this;
    }

    protected void checkHandler(List<RunHandler<RunContext>> handlers) {
        if (CollectionUtils.isEmpty(handlers)) {
            throw new IllegalArgumentException("Handler list cannot be empty");
        }

        List<String> phaseTypeNames = handlers.stream().map(RunHandler::phase).map(Object::getClass).map(Class::getSimpleName).distinct().toList();
        if (phaseTypeNames.size() > 1) {
            throw new IllegalArgumentException("All handlers must be of the same type, types in this batch: " + String.join(", ", phaseTypeNames));
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

