package top.huzz.resilix.callback;


import jakarta.annotation.Nullable;
import top.huzz.resilix.core.RunContext;

/**
 * Phase callback interface
 *
 * @author chenji
 * @since 1.0.0
 */
public interface PhaseCallback {
    /**
     * Asynchronous phase callback
     *
     * @param context run context
     * @param extra   additional information
     */
    void callback(RunContext context, @Nullable Object extra);
}
