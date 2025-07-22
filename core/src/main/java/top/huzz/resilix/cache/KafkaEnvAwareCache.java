package top.huzz.resilix.cache;


/**
 * 基于环境的kafkaTemplate缓存
 *
 * @author chenji
 * @since 1.0.0
 */
public class KafkaEnvAwareCache extends AbstractAwareCache implements EnvAwareCache {
    @Override
    public Type getType() {
        return Type.KAFKA;
    }
}
