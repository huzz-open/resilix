package org.huzz.resilix.api.run;

import jakarta.annotation.Nonnull;
import org.huzz.resilix.api.idempotent.IdempotentJudge;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

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
     * @throws BeanInstantiationException if the context class cannot be instantiated.
     */
    default void start() throws BeanInstantiationException {
        RunContext runContext = BeanUtils.instantiateClass(getCxtClass());
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
