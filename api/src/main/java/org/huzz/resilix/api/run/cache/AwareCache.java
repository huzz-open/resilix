package org.huzz.resilix.api.run.cache;


import org.huzz.resilix.api.run.RunContext;

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
        KAFKA, REDISSON, EXECUTION_UNIT, IDEMPOTENT_JUDGE
    }
}
