package top.huzz.resilix.recorder;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import top.huzz.resilix.core.RunContext;

/**
 * Phase recorder. Each phase can have a recorder to record the execution results,
 * such as storing data to Redis or database.
 *
 * @author chenji
 * @since 1.0.0
 */
public interface PhaseRecorder<C extends RunContext> {

    /**
     * Phase start - about to begin execution
     *
     * @param context context object
     */
    void readyFor(C context);

    /**
     * Phase end
     *
     * @param context context object
     * @param e       phase exception
     */
    void end(C context, @Nullable Exception e);

    /**
     * @return recorder type
     */
    @Nonnull
    default Type getType() {
        return Type.NOPE;
    }

    enum Type {
        NOPE, PLAN, EXECUTION_UNIT
    }
}
