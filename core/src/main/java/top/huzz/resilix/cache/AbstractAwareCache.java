package top.huzz.resilix.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于线程的环境感知缓存，只适合在本地环境使用，不适合在分布式环境使用。
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
