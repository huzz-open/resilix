package top.huzz.resilix.cache;


import top.huzz.resilix.core.RunContext;

/**
 * @author chenji
 * @since 1.0.0
 */
public interface AwareCache extends Cleanable {
    void put(Object key, Object value);

    Object get(Object key);

    void remove(Object key);

    default void clean(RunContext context) {
    }

    Type getType();

    enum Type {
        KAFKA, REDISSON, IDEMPOTENT_JUDGE
    }
}
