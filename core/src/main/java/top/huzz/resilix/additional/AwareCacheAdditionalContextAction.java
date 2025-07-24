package top.huzz.resilix.additional;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import top.huzz.resilix.core.AwareCacheRunContext;
import top.huzz.resilix.cache.AwareCache;

/**
 * Context-based additional cache operation
 *
 * @param <C> context type
 * @author chenji
 * @since 1.0.0
 */
public interface AwareCacheAdditionalContextAction<C extends AwareCacheRunContext> extends AdditionalContextAction<C> {

    /**
     * Calculate the object to be used as cache key
     *
     * @param context context
     * @return cache key
     */
    @Nonnull
    Object calculateKey(C context);

    /**
     * Calculate cache, only cached when not empty.
     * This method is executed only when useActionCache returns false or no cache result is found.
     *
     * @param context context
     * @return cached object
     */
    Object calculateValue(C context);


    /**
     * Used to control whether the calculate method is executed.
     *
     * @return true: when execution result can be obtained from Cache, do not execute calculate method, otherwise do not execute; false: execute calculate method
     */
    default boolean useCalculateCache() {
        return false;
    }

    @Override
    default void action(C context) {
        if (skip(context)) {
            return;
        }

        Object key = calculateKey(context);
        AwareCache envAwareCache = context.awareCache(getToBeUseAwareCacheType());
        if (useCalculateCache()
                && envAwareCache.get(key) != null) {
            return;
        }
        Object value = calculateValue(context);
        if (value == null) {
            return;
        }
        envAwareCache.put(key, value);
    }

    /**
     * @return Returns the type of cache to be used
     */
    @Nonnull
    AwareCache.Type getToBeUseAwareCacheType();
}
