package top.huzz.resilix.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-based environment-aware cache, suitable only for local environments,
 * not suitable for distributed environments.
 *
 * @author chenji
 * @since 1.0.0
 */
public abstract class AbstractAwareCache implements AwareCache {
    private final Map<Object, Object> cache = new ConcurrentHashMap<>();

    @Override
    public void put(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object get(Object key) {
        return cache.get(key);
    }

    @Override
    public void remove(Object key) {
        cache.remove(key);
    }

    protected Map<Object, Object> getCache() {
        return cache;
    }
}
