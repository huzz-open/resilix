package top.huzz.resilix.idempotent;


import jakarta.annotation.Nullable;

/**
 * Preceding idempotent judgment interface
 *
 * @author chenji
 * @since 1.0.0
 */
public interface PrecedingIdempotentJudge extends IdempotentJudge {

    /**
     * Pre-judgment, if it returns false, the {@link #judge(IdempotentKey)} method will not be executed
     *
     * @param key idempotent identifier
     * @return true: continue execution; false: do not execute
     */
    default boolean preJudge(@Nullable IdempotentKey key) {
        return false;
    }

    /**
     * Determines whether it has been executed. If key is null, returns false
     *
     * @param key idempotent identifier
     * @return true: already executed; false: not executed
     */
    default boolean judge(@Nullable IdempotentKey key) {
        if (key == null) {
            return false;
        }
        if (preJudge(key)) {
            // If pre-judgment is true, it means it has definitely been executed
            return true;
        }
        return doJudge(key);
    }

    /**
     * Determines whether it has been executed. If key is null, returns false
     *
     * @param key idempotent identifier
     * @return true: already executed; false: not executed
     */
    boolean doJudge(@Nullable IdempotentKey key);
}
