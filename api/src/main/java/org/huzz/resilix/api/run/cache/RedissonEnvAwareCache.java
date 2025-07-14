package org.huzz.resilix.api.run.cache;

import org.springframework.stereotype.Component;

/**
 * 基于环境的Redisson缓存
 *
 * @author chenji
 * @since 1.0.0
 */
@Component
public class RedissonEnvAwareCache extends AbstractAwareCache implements EnvAwareCache {
    @Override
    public Type getType() {
        return Type.REDISSON;
    }
}
