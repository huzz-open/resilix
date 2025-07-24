package top.huzz.resilix.predicate;


import top.huzz.resilix.core.RunContext;
import top.huzz.resilix.recorder.PhaseRecorder;
import top.huzz.resilix.handler.RunHandler;

/**
 * Run predicate for handlers
 * <p>
 * Since sometimes it's necessary to determine whether to skip the current handler based on different contexts 
 * in a processing chain, this interface is defined. For example: scenarios and test cases have almost identical 
 * execution processes, except for some special processing, so this interface can be used to determine whether 
 * to skip the current handler, enabling handler reuse.
 *
 * @param <C> context type
 * @author chenji
 * @see PhaseRecorder
 * @see RunHandler
 * @since 1.0.0
 */
public interface HandlerRunPredicate<C extends RunContext> {
    /**
     * Determines whether to execute the current handler. If it returns false (not executed), 
     * then the current handler will not be executed (including handle, postHandle, finallyHandle methods), 
     * and the execution status of the current handler will not be recorded by PhaseRecorder.
     *
     * @param context context
     * @return true: run the handler, false: do not run the handler
     */
    boolean shouldRun(C context);
}
