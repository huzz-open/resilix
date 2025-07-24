package top.huzz.resilix.cache;


import top.huzz.resilix.core.RunContext;

/**
 * Environment-aware cache interface for storing key-value pairs with different storage backends.
 * Supports various cache types like Kafka, Redisson, and idempotent judgment caches.
 *
 * @author chenji
 * @since 1.0.0
 */
public interface AwareCache extends Cleanable {
    /**
     * Store a key-value pair in the cache
     *
     * @param key   the key to store
     * @param value the value to store
     */
    void put(Object key, Object value);

    /**
     * Retrieve a value from the cache by key
     *
     * @param key the key to look up
     * @return the value associated with the key, or null if not found
     */
    Object get(Object key);

    /**
     * Remove a key-value pair from the cache
     *
     * @param key the key to remove
     */
    void remove(Object key);

    /**
     * Clean up the cache based on the run context
     *
     * @param context the run context
     */
    default void clean(RunContext context) {
    }

    /**
     * Get the type of this cache instance
     *
     * @return the cache type
     */
    Type getType();

    /**
     * Enumeration of supported cache types
     */
    enum Type {
        /** Kafka-related cache */
        KAFKA, 
        /** Redisson Redis cache */
        REDISSON, 
        /** Idempotent judgment cache */
        IDEMPOTENT_JUDGE
    }
}
