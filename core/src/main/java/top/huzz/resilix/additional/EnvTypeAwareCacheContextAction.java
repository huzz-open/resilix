package top.huzz.resilix.additional;


import jakarta.annotation.Nonnull;
import top.huzz.resilix.core.AwareCacheRunContext;
import top.huzz.resilix.cache.AwareCache;

/**
 * 基于环境缓存附加操作
 *
 * @param <C> 上下文
 * @author chenji
 * @since 1.0.0
 */
public abstract class EnvTypeAwareCacheContextAction<C extends AwareCacheRunContext> implements AwareCacheAdditionalContextAction<C> {


    @Nonnull
    @Override
    public Object calculateKey(C context) {
        return context.getEnvType();
    }

    /**
     * @return 返回需要使用的环境感知缓存类型
     */
    @Nonnull
    public abstract AwareCache.Type getToBeUseAwareCacheType();

}
