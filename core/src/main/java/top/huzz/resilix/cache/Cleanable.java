package top.huzz.resilix.cache;


import top.huzz.resilix.core.RunContext;

/**
 * Interface for objects that can be cleaned up based on a run context.
 * This interface provides a standard way to perform cleanup operations
 * that may depend on the current execution context.
 *
 * @author chenji
 * @since 1.0.0
 */
public interface Cleanable {
    /**
     * Performs cleanup operations based on the provided run context.
     * Implementations should define specific cleanup logic appropriate
     * for their use case.
     *
     * @param context the run context that may influence cleanup behavior
     */
    void clean(RunContext context);
}
