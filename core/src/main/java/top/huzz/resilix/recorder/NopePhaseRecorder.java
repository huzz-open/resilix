package top.huzz.resilix.recorder;


import jakarta.annotation.Nullable;
import top.huzz.resilix.core.SimpleRunContext;

/**
 * No-operation phase recorder implementation that does nothing.
 * This is used as a default recorder when no specific recording behavior is needed.
 *
 * @author chenji
 * @since 1.0.0
 */
public class NopePhaseRecorder implements PhaseRecorder<SimpleRunContext> {

    /**
     * {@inheritDoc}
     * This implementation does nothing.
     */
    @Override
    public void readyFor(SimpleRunContext context) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     * This implementation does nothing.
     */
    @Override
    public void end(SimpleRunContext context, @Nullable Exception e) {
        // do nothing
    }
}
