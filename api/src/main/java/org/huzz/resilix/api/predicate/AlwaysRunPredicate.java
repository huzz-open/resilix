package org.huzz.resilix.api.predicate;


import org.huzz.resilix.api.run.RunContext;

/**
 * @param <C> 上下文类型
 * @author chenji
 * @since 1.0.0
 */
public class AlwaysRunPredicate<C extends RunContext> implements HandlerRunPredicate<C> {
    @Override
    public boolean shouldRun(C context) {
        return true;
    }
}
