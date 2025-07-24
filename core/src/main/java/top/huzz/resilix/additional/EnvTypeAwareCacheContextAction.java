package top.huzz.resilix.additional;


import jakarta.annotation.Nonnull;
import top.huzz.resilix.core.AwareCacheRunContext;
import top.huzz.resilix.cache.AwareCache;

/**
 * Environment cache-based additional context action
 *
 * @param <C> context type
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
     * @return Returns the type of environment-aware cache to be used
     */
    @Nonnull
    public abstract AwareCache.Type getToBeUseAwareCacheType();

}
