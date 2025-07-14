package org.huzz.resilix.api.run.callback;


import org.huzz.resilix.api.run.RunContext;

/**
 * @author chenji
 * @since 1.0.0
 */
public class NopePhaseCallback implements PhaseCallback {
    @Override
    public void callback(RunContext context, Object extra) {
        // do nothing
    }
}
