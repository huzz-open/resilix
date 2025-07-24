package top.huzz.resilix.idempotent;

/**
 * Idempotent run context interface
 *
 * @author chenji
 * @since 1.0.0
 */
public interface IdempotentKey {
    /**
     * Get the idempotent identifier, used to determine if it's the same task.
     * The same value is considered the same task.
     *
     * @return idempotent identifier
     */
    String idempotentKey();
}
