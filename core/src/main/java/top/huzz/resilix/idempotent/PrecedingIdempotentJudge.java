package top.huzz.resilix.idempotent;


import jakarta.annotation.Nullable;

/**
 * 幂等判断接口
 *
 * @author chenji
 * @since 1.0.0
 */
public interface PrecedingIdempotentJudge extends IdempotentJudge {

    /**
     * 预判断，如果返回false，则不会执行{@link #judge(IdempotentKey)}方法
     *
     * @param key 幂等标识
     * @return true：继续执行；false：不执行
     */
    default boolean preJudge(@Nullable IdempotentKey key) {
        return false;
    }

    /**
     * 判断是否已经执行过，如果key为null，则返回false
     *
     * @param key 幂等标识
     * @return true：已经执行过；false：未执行过
     */
    default boolean judge(@Nullable IdempotentKey key) {
        if (key == null) {
            return false;
        }
        if (preJudge(key)) {
            // 如果预判断为true，那表示肯定已经执行过了
            return true;
        }
        return doJudge(key);
    }

    /**
     * 判断是否已经执行过，如果key为null，则返回false
     *
     * @param key 幂等标识
     * @return true：已经执行过；false：未执行过
     */
    boolean doJudge(@Nullable IdempotentKey key);
}
