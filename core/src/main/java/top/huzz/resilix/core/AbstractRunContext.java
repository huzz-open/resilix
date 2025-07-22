package top.huzz.resilix.core;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import top.huzz.resilix.constants.EnvType;
import top.huzz.resilix.cache.AwareCache;

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
    // 环境类型
    protected EnvType envType;
    // 额外信息
    @JsonIgnore
    private Object extra;

    @Override
    public RunContext duplicate() {
        return JSON.parseObject(JSON.toJSONString(this), this.getClass());
    }

    @Override
    public void cover(RunContext anotherContext) {
        try {
            BeanUtils.copyProperties(this, anotherContext);
        } catch (Exception e) {
            throw new RuntimeException("Failed to cover run context", e);
        }
    }

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
