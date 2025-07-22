package top.huzz.resilix.cache;

/**
 * 基于环境的Redisson缓存
 *
 * @author chenji
 * @since 1.0.0
 */
public class RedissonEnvAwareCache extends AbstractAwareCache implements EnvAwareCache {
    @Override
    public Type getType() {
        return Type.REDISSON;
    }
}
