package top.huzz.resilix.core;

import top.huzz.resilix.idempotent.IdempotentJudge;

/**
 * Finally handler interface
 * 
 * @author chenji
 * @see IdempotentJudge
 * @since 1.0.0
 */
public interface FinallyHandler<C extends RunContext> {
    /**
     * Logic that will definitely be executed after the entire processing chain is completed,
     * regardless of whether the final result is success or failure, 
     * and regardless of whether it's a duplicate operation (determined by idempotent judge).
     * <pre>
     * Assuming there are multiple phases A-B-C-D-E, where A-B are execution phases of server X,
     * and C-D-E are execution phases of server Y, all executing serially.
     * If you need to ensure that a finallyHandle is fully executed, 
     * you need to call the finallyHandle method in the last phase of each server,
     * that is, call finallyHandle in phases B and E.
     * </pre>
     *
     * @param context run context
     */
    default void finallyHandle(C context) {
        // do nothing
    }
}
