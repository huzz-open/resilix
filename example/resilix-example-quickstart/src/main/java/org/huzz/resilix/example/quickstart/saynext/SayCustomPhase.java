package org.huzz.resilix.example.quickstart.saynext;

import org.huzz.resilix.api.run.Phase;

/**
 * @author chenji
 * @since 1.0.0
 */
public enum SayCustomPhase implements Phase {
    SAY_HELLO,
    SAY_WORLD,

    ;

    @Override
    public Phase[] getValues() {
        return values();
    }
}
