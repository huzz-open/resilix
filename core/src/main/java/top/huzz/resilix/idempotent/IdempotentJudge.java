package top.huzz.resilix.idempotent;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Idempotent judgment interface
 *
 * @author chenji
 * @since 1.0.0
 */
public interface IdempotentJudge {
    /**
     * Determines whether it has been executed. If key is null, returns false
     *
     * @param key idempotent identifier
     * @return true: already executed; false: not executed
     */
    boolean judge(@Nullable IdempotentKey key);

    /**
     * Put the key into the idempotent judge
     *
     * @param key idempotent identifier
     */
    void put(@Nonnull IdempotentKey key);

    /**
     * Destroy
     */
    void destroy();
}
