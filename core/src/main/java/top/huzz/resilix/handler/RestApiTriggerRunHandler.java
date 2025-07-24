package top.huzz.resilix.handler;


import top.huzz.resilix.core.RunContext;

/**
 * RestApi trigger execution handler
 *
 * @param <C> context type
 * @author chenji
 * @since 1.0.0
 */
public interface RestApiTriggerRunHandler<C extends RunContext> extends RunHandler<C> {
}
