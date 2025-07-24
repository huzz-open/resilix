package top.huzz.resilix.handler;


import jakarta.annotation.Nonnull;
import top.huzz.resilix.predicate.AlwaysRunPredicate;
import top.huzz.resilix.predicate.HandlerRunPredicate;
import top.huzz.resilix.recorder.NopePhaseRecorder;
import top.huzz.resilix.recorder.PhaseRecorder;
import top.huzz.resilix.core.FinallyHandler;
import top.huzz.resilix.core.Phase;
import top.huzz.resilix.core.RunContext;

import java.util.List;

/**
 * Run handler interface
 *
 * @param <C> context type
 * @author chenji
 * @since 1.0.0
 */
public interface RunHandler<C extends RunContext> extends FinallyHandler<C> {
    /**
     * Core logic of the handler
     *
     * @param context execution context
     * @throws Exception When an unignorable exception occurs during processing, 
     *                  an exception is thrown. This indicates that the processing result is failed,
     *                  and the next handler will not be executed.
     */
    void handle(C context) throws Exception;

    /**
     * @return The test plan run phase that this handler can process
     */
    @Nonnull
    Phase phase();

    /**
     * Post-processing for a single RunHandler, executed regardless of whether the handle method throws an exception
     *
     * @param context run context
     */
    default void postHandle(C context) {
        // do nothing
    }

    /**
     * @return Phase recorder
     */
    @SuppressWarnings("unchecked")
    default PhaseRecorder<C> getRecorder() {
        return (PhaseRecorder<C>) new NopePhaseRecorder();
    }

    /**
     * @return List of run predicate types
     */
    @Nonnull
    default List<HandlerRunPredicate<C>> runPredicate() {
        return List.of(new AlwaysRunPredicate<>());
    }
}
