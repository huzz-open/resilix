package top.huzz.resilix.example.quickstart.sayhello;

import top.huzz.resilix.core.Phase;

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
