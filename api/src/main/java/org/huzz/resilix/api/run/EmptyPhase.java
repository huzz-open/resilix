package org.huzz.resilix.api.run;

/**
 * @author chenji
 * @since 1.0.0
 */
public enum EmptyPhase implements Phase {

    ;

    @Override
    public Phase[] getValues() {
        return values();
    }
}
