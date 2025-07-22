package top.huzz.resilix.core;

import jakarta.annotation.Nonnull;
import top.huzz.resilix.exception.NewInstanceException;
import top.huzz.resilix.idempotent.IdempotentJudge;

/**
 * RunHandlerManager is responsible for managing the run process of a specific phase.
 *
 * @author chenji
 * @since 1.0.0
 */
public interface RunHandlerManager {
    /**
     * start run process with the given context.
     *
     * @param context the run context, which contains the data needed for the run process.
     * @throws NullPointerException     if the context is null.
     * @throws IllegalArgumentException if the context is not valid or does not match the expected type for the run process.
     */
    void start(RunContext context) throws NullPointerException, IllegalArgumentException;

    /**
     * Starts the run process using a default context instance created from {@link #getCxtClass()}.
     *
     * @throws NewInstanceException if the context class cannot be instantiated, which may occur if the class does not have a no-argument constructor or if instantiation fails for other reasons.
     */
    default void start() throws NewInstanceException {
        Class<? extends RunContext> cxtClass = getCxtClass();
        RunContext runContext;
        try {
            runContext = cxtClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new NewInstanceException(cxtClass, e);
        }
        start(runContext);
    }

    /**
     * @return the class of the run context that this manager is responsible for.
     */
    @Nonnull
    Class<? extends RunContext> getCxtClass();

    /**
     * Add an idempotent judge to the run handler manager for a specific phase.
     *
     * @param phase           the phase for which the idempotent judge is being added.
     * @param idempotentJudge the idempotent judge to be added, which will determine if the run process should be skipped based on idempotency checks.
     * @return the current instance of RunHandlerManager, allowing for method chaining.
     */
    RunHandlerManager addIdempotentJudge(Phase phase, IdempotentJudge idempotentJudge);
}
