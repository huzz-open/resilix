package top.huzz.resilix.core;

/**
 * RunHandlerManagerFactory is a factory interface for creating instances of RunHandlerManager.
 *
 * @author chenji
 * @since 1.0.0
 */
public interface RunHandlerManagerFactory {
    /**
     * build RunHandlerManager
     *
     * @param phaseClass phase class
     * @return RunHandlerManager
     */
    RunHandlerManager build(Class<? extends Phase> phaseClass);
}
