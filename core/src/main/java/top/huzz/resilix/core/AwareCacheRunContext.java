package top.huzz.resilix.core;


import top.huzz.resilix.cache.AwareCache;

import java.util.Map;

/**
 * @author chenji
 * @since 1.0.0
 */
public interface AwareCacheRunContext extends RunContext {
    AwareCache awareCache(AwareCache.Type type);

    void trySetUpEnvAwareCacheMap(Map<AwareCache.Type, AwareCache> awareCacheMap);
}
