package top.huzz.resilix.idempotent;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * 幂等判断接口
 *
 * @author chenji
 * @since 1.0.0
 */
public interface IdempotentJudge {
    /**
     * 判断是否已经执行过，如果key为null，则返回false
     *
     * @param key 幂等标识
     * @return true：已经执行过；false：未执行过
     */
    boolean judge(@Nullable IdempotentKey key);

    /**
     * 将key放入幂等判断器中
     *
     * @param key 幂等标识
     */
    void put(@Nonnull IdempotentKey key);

    /**
     * 销毁
     */
    void destroy();
}
