package top.huzz.resilix.core;

import jakarta.annotation.Nullable;
import top.huzz.resilix.idempotent.IdempotentJudge;
import top.huzz.resilix.idempotent.IdempotentKey;

import java.util.concurrent.ExecutorService;

/**
 * 阶段定义，用于表示一系列具备执行顺序的逻辑
 *
 * @author chenji
 * @since 1.0.0
 */
public interface Phase {
    /**
     * 序号，值越小越靠前，不能重复。
     *
     * @return 阶段编号
     */
    int ordinal();

    /**
     * @return 是否异步执行
     */
    default boolean isAsync() {
        return false;
    }

    /**
     * 有一些阶段是需要做幂等处理的，比如Kafka消费阶段，这类消息可能在rebalance的时候会重复消费。
     * 这里可以定义是否做幂等处理，但需要注意的是，该阶段所在的下文必须实现{@link IdempotentKey}接口。
     * 如果调用{@link IdempotentKey#idempotentKey()}方法返回的key相同，则认为是同一个任务，再由具体的幂等处理器判断是否已经执行过。
     *
     * @return 幂等处理器
     * @see IdempotentKey
     * @see IdempotentJudge
     */
    @Nullable
    default IdempotentJudge idempotentJudge() {
        return null;
    }

    /**
     * 如果是异步的任务，可以使用该方法自定义线程池
     *
     * @return 自定义线程池
     */
    @Nullable
    default ExecutorService customExecutor() {
        return null;
    }

    /**
     * @return 是否废弃
     */
    default boolean isDeprecated() {
        return false;
    }

    /**
     * @return 按照顺序返回阶段
     */
    Phase[] getValues();

    /**
     * @return 下一个阶段
     */
    default Phase next() {
        if (ordinal() == getValues().length - 1) {
            return null;
        }
        return getValues()[ordinal() + 1];
    }
}
