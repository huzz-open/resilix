package top.huzz.resilix.core;


import top.huzz.resilix.exception.PhaseStoppedException;

/**
 * Phase stop status checker interface
 *
 * @author chenji
 * @since 1.0.0
 */
public interface PhaseStopStatusChecker {

    /**
     * Check if the phase status is stopped
     *
     * @param context context
     * @throws PhaseStoppedException when phase is stopped, the execution manager should handle this exception to deal with the stop status
     */
    void check(RunContext context) throws PhaseStoppedException;
}
