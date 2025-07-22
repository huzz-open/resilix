package top.huzz.resilix.example.usectx.saynext;

import top.huzz.resilix.core.Phase;

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
