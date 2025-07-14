package org.huzz.resilix.api.recorder;


import jakarta.annotation.Nullable;
import org.huzz.resilix.api.run.SimpleRunContext;

/**
 * @author chenji
 * @since 1.0.0
 */

public class NopePhaseRecorder implements PhaseRecorder<SimpleRunContext> {

    @Override
    public void readyFor(SimpleRunContext context) {
        // do nothing
    }

    @Override
    public void end(SimpleRunContext context, @Nullable Exception e) {
        // do nothing
    }
}
