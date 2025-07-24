package top.huzz.resilix.core;

/**
 * Provides additional information for the execution context
 *
 * @param <C> context type
 * @author chenji
 * @since 1.0.0
 */
public interface ExtraInfoProvider<C extends RunContext> {
    /**
     * Get additional information
     * 
     * @param context test plan run context
     * @return additional information
     */
    Object apply(C context);
}

