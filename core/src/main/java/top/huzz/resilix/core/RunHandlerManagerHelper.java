package top.huzz.resilix.core;

/**
 * Utility class providing helper methods for RunHandlerManager operations.
 * This class contains static utility methods to assist with common tasks
 * related to handler management and execution.
 *
 * @author chenji
 * @since 1.0.0
 */
public final class RunHandlerManagerHelper {
    private static RunHandlerManagerFactory runHandlerManagerFactory;

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always thrown to prevent instantiation
     */
    private RunHandlerManagerHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Builds a RunHandlerManager instance for the specified Phase class.
     *
     * @param cxtClass the Phase class to build the RunHandlerManager for
     * @return a RunHandlerManager instance for the specified phase
     * @throws IllegalStateException if the runHandlerManagerFactory has not been initialized
     */
    public static RunHandlerManager build(Class<? extends Phase> cxtClass) {
        if (runHandlerManagerFactory == null) {
            throw new IllegalStateException("RunHandlerManagerFactory has not been initialized. " +
                    "Please ensure the resilix-spring-boot-starter is properly configured.");
        }
        return runHandlerManagerFactory.build(cxtClass);
    }

    /**
     * Sets the RunHandlerManagerFactory instance.
     *
     * @param factory the RunHandlerManagerFactory instance to set
     */
    @SuppressWarnings("lombok")
    public static void setRunHandlerManagerFactory(RunHandlerManagerFactory factory) {
        runHandlerManagerFactory = factory;
    }
}
