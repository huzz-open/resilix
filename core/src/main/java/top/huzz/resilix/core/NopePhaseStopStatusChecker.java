package top.huzz.resilix.core;


import top.huzz.resilix.exception.PhaseStoppedException;

/**
 * No-operation phase stop status checker that does nothing.
 * This is used as a default implementation when no specific stop status checking is needed.
 *
 * @author chenji
 * @since 1.0.0
 */
public class NopePhaseStopStatusChecker implements PhaseStopStatusChecker {

    @Override
    public void check(RunContext context) throws PhaseStoppedException {
        // do nothing
    }
}
