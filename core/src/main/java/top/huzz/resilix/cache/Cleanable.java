package top.huzz.resilix.cache;


import top.huzz.resilix.core.RunContext;

/**
 * @author chenji
 * @since 1.0.0
 */
public interface Cleanable {
    void clean(RunContext context);
}
