package top.huzz.resilix.additional;


import jakarta.annotation.Nonnull;
import top.huzz.resilix.core.AwareCacheRunContext;
import top.huzz.resilix.cache.AwareCache;

/**
 * 基于context的附加缓存操作
 *
 * @param <C> 上下文
 * @author chenji
 * @since 1.0.0
 */
public interface AwareCacheAdditionalContextAction<C extends AwareCacheRunContext> extends AdditionalContextAction<C> {

    /**
     * 计算要作为缓存key的对象
     *
     * @param context 上下文
     * @return 缓存key
     */
    @Nonnull
    Object calculateKey(C context);

    /**
     * 计算缓存，只有不为空的时候才会被缓存。
     * 当且仅当useActionCache返回false或查不到缓存结果时，才会执行这个方法。
     *
     * @param context 上下文
     * @return 被缓存的对象
     */
    Object calculateValue(C context);


    /**
     * 用于控制calculate方法是否执行。
     *
     * @return true：当能从Cache中获取到执行结果的时候则不执行calculate方法，否则不执行；false：执行calculate方法
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
     * @return 返回需要使用的缓存类型
     */
    @Nonnull
    AwareCache.Type getToBeUseAwareCacheType();
}
