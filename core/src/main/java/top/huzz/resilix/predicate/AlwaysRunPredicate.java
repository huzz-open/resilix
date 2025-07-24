package top.huzz.resilix.predicate;


import top.huzz.resilix.core.RunContext;

/**
 * A predicate that always returns true, meaning the handler will always be executed.
 * This is the default predicate used when no specific execution conditions are needed.
 *
 * @param <C> context type
 * @author chenji
 * @since 1.0.0
 */
public class AlwaysRunPredicate<C extends RunContext> implements HandlerRunPredicate<C> {
    /**
     * {@inheritDoc}
     * Always returns true to indicate that the handler should be executed.
     *
     * @param context the run context
     * @return always true
     */
    @Override
    public boolean shouldRun(C context) {
        return true;
    }
}
