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
 * Abstract base implementation of RunContext providing common functionality.
 * This class implements both RunContext and AwareCacheRunContext interfaces
 * and provides default implementations for context operations.
 *
 * @author chenji
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class AbstractRunContext implements RunContext, AwareCacheRunContext {

    @JsonIgnore
    protected volatile static Map<AwareCache.Type, AwareCache> awareCacheMap = null;

    /** Whether the current phase is successful */
    protected boolean success;
    /** Exception from the current phase */
    @JsonIgnore
    protected Exception exception;
    /** Current phase */
    protected Phase currentPhase;
    /** Whether execution has stopped */
    protected boolean isStopped;
    /** Whether skipped due to idempotent judgment */
    protected boolean isSkipped;
    /** Environment type */
    protected EnvType envType;
    /** Additional information */
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
