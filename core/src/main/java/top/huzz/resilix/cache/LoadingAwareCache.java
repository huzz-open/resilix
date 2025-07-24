package top.huzz.resilix.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import top.huzz.resilix.core.RunContext;

import java.util.concurrent.TimeUnit;

/**
 * Thread-based environment-aware cache, suitable only for local environments,
 * not suitable for distributed environments.
 *
 * @author chenji
 * @since 1.0.0
 */
public abstract class LoadingAwareCache implements AwareCache {

    public final Cache<Object, Object> cache;
    private final boolean accessRenewal;

    protected LoadingAwareCache() {
        this(600, 500000, true);
    }

    protected LoadingAwareCache(long expireAfterWriteSecond, long maximumSize, boolean accessRenewal) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expireAfterWriteSecond, TimeUnit.SECONDS)
                .maximumSize(maximumSize)
                .build();
        this.accessRenewal = accessRenewal;
    }

    @Override
    public Object get(Object key) {
        Object o = cache.getIfPresent(key);
        if (accessRenewal && o != null) {
            // After setting expireAfterAccess officially, the key will be deleted directly after the expireAfterAccess time 
            // has passed since the first access. Here we put it back to implement access renewal functionality
            put(key, o);
        }
        return o;
    }

    @Override
    public void remove(Object key) {
        cache.invalidate(key);
    }

    @Override
    public void put(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void clean(RunContext context) {
        cache.cleanUp();
    }
}
