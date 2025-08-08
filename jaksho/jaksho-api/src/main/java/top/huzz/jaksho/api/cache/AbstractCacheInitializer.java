package top.huzz.jaksho.api.cache;

import jakarta.annotation.Nonnull;
import top.huzz.jaksho.api.cache.exception.CacheInitException;

import java.util.Map;
import java.util.Objects;

/**
 * @author huzz
 * @since 1.0.2
 */
public abstract class AbstractCacheInitializer implements CacheInitializer {

    protected final Map<Object, Object> cache;

    public AbstractCacheInitializer(@Nonnull Map<Object, Object> cache) {
        Objects.requireNonNull(cache);
        this.cache = cache;
    }

    @Override
    public void init() throws CacheInitException {
        Map<Object, Object> all = findAll();
        cache.putAll(all);
    }

    @Nonnull
    protected abstract Map<Object, Object> findAll();

}
