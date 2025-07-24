package top.huzz.resilix.callback;


import top.huzz.resilix.core.RunContext;

/**
 * No-operation phase callback implementation that does nothing.
 * This is used as a default callback when no specific callback behavior is needed.
 *
 * @author chenji
 * @since 1.0.0
 */
public class NopePhaseCallback implements PhaseCallback {
    /**
     * {@inheritDoc}
     * This implementation does nothing.
     */
    @Override
    public void callback(RunContext context, Object extra) {
        // do nothing
    }
}
