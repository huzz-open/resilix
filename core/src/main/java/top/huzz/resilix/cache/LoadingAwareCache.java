package top.huzz.resilix.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import top.huzz.resilix.core.RunContext;

import java.util.concurrent.TimeUnit;

/**
 * 基于线程的环境感知缓存，只适合在本地环境使用，不适合在分布式环境使用。
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
            // 官方的expireAfterAccess设置后，在首次访问后，在过了expireAfterAccess后会直接把key删除掉，这里重新put回去，实现访问续期功能
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
