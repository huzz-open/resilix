package org.huzz.resilix.api.run;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.huzz.resilix.api.run.cache.AwareCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenji
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class AbstractRunContext implements RunContext, AwareCacheRunContext {

    @JsonIgnore
    protected volatile static Map<AwareCache.Type, AwareCache> awareCacheMap = null;

    // 当前阶段是否成功
    protected boolean success;
    // 当前阶段异常
    @JsonIgnore
    protected Exception exception;
    // 当前阶段
    protected Phase currentPhase;
    // 是否已经停止
    protected boolean isStopped;
    // 是否因为幂等判断而跳过
    protected boolean isSkipped;

    @Override
    public AwareCache awareCache(AwareCache.Type type) {
        return AbstractRunContext.awareCacheMap.get(type);
    }

    @Override
    public void trySetUpEnvAwareCacheMap(Map<AwareCache.Type, AwareCache> awareCacheMap) {
        if (AbstractRunContext.awareCacheMap == null) {
            synchronized (AbstractRunContext.class) {
                if (AbstractRunContext.awareCacheMap == null) {
                    AbstractRunContext.awareCacheMap = new ConcurrentHashMap<>(awareCacheMap);
                }
            }
        } else {
            AbstractRunContext.awareCacheMap.putAll(awareCacheMap);
        }
    }
}
