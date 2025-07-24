package top.huzz.resilix.core;

import jakarta.annotation.Nullable;
import top.huzz.resilix.idempotent.IdempotentJudge;
import top.huzz.resilix.idempotent.IdempotentKey;

import java.util.concurrent.ExecutorService;

/**
 * Phase definition, used to represent a series of logic with execution order
 *
 * @author chenji
 * @since 1.0.0
 */
public interface Phase {
    /**
     * Sequence number, smaller values come first, cannot be duplicated.
     *
     * @return phase number
     */
    int ordinal();

    /**
     * @return whether to execute asynchronously
     */
    default boolean isAsync() {
        return false;
    }

    /**
     * Some phases need idempotent processing, such as Kafka consumption phase, 
     * which may consume messages repeatedly during rebalance.
     * Here you can define whether to do idempotent processing, but note that 
     * the context where this phase is located must implement the {@link IdempotentKey} interface.
     * If the key returned by calling {@link IdempotentKey#idempotentKey()} method is the same, 
     * it is considered the same task, and then the specific idempotent processor determines 
     * whether it has been executed.
     *
     * @return idempotent processor
     * @see IdempotentKey
     * @see IdempotentJudge
     */
    @Nullable
    default IdempotentJudge idempotentJudge() {
        return null;
    }

    /**
     * If it's an asynchronous task, you can use this method to customize the thread pool
     *
     * @return custom thread pool
     */
    @Nullable
    default ExecutorService customExecutor() {
        return null;
    }

    /**
     * @return whether deprecated
     */
    default boolean isDeprecated() {
        return false;
    }

    /**
     * @return return phases in order
     */
    Phase[] getValues();

    /**
     * @return next phase
     */
    default Phase next() {
        if (ordinal() == getValues().length - 1) {
            return null;
        }
        return getValues()[ordinal() + 1];
    }
}
