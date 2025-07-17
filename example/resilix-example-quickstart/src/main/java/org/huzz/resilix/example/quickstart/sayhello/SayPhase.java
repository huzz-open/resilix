package org.huzz.resilix.example.quickstart.sayhello;

import org.huzz.resilix.api.run.Phase;

/**
 * @author chenji
 * @since 1.0.0
 */
public enum SayPhase implements Phase {
    SAY_HELLO,
    SAY_WORLD,

    ;

    @Override
    public Phase[] getValues() {
        return values();
    }
}
