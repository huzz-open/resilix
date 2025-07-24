package top.huzz.resilix.predicate;


import top.huzz.resilix.core.RunContext;

/**
 * For deprecated phases, it's better not to directly remove the enum values, 
 * otherwise parsing existing data will cause enum errors. 
 * This predicate can be used to avoid execution.
 *
 * @param <C> context type
 * @author chenji
 * @since 1.0.0
 */
public class NeverRunPredicate<C extends RunContext> implements HandlerRunPredicate<C> {
    @Override
    public boolean shouldRun(C context) {
        return false;
    }
}
