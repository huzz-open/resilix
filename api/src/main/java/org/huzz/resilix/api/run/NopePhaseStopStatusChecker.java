package org.huzz.resilix.api.run;


import org.huzz.resilix.api.run.exception.PhaseStoppedException;

/**
 * 什么也不做的状态的停止状态检查器
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
