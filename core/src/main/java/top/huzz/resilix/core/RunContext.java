package top.huzz.resilix.core;

import top.huzz.resilix.constants.EnvType;

import java.util.HashMap;
import java.util.Map;

/**
 * the run context interface, which is used to store the context of the current run.
 *
 * @author chenji
 * @since 1.0.0
 */
public interface RunContext {
    /**
     * Enum representing the different phases of a run context.
     */
    ThreadLocal<Map<Class<? extends RunContext>, RunContext>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    /**
     * set success status of the run context.
     *
     * @param success true if the run is successful, false otherwise.
     */
    void setSuccess(boolean success);

    /**
     * @return true if the run is successful, false otherwise.
     */
    boolean isSuccess();

    /**
     * set exception for the run context.
     *
     * @param e the exception that occurred during the run.
     */
    void setException(Exception e);

    /**
     * set the current phase of the run context.
     *
     * @param phase the current phase of the run.
     */
    void setCurrentPhase(Phase phase);

    /**
     * @return the exception that occurred during the run, or null if no exception occurred.
     */
    Phase getCurrentPhase();

    /**
     * @return true if the run context is stopped, false otherwise.
     */
    boolean isStopped();

    /**
     * Sets the stopped status of the run context.
     *
     * @param stopped true if the run context is stopped, false otherwise.
     */
    void setStopped(boolean stopped);

    /**
     * Sets the skipped status of the run context.
     *
     * @param skip true if the run context should be skipped, false otherwise.
     */
    void setSkipped(boolean skip);

    /**
     * @return env type of the run context, which indicates the environment in which the run is executed.
     */
    EnvType getEnvType();

    /**
     * @return duplicated run context, which is a copy of the current run context.
     */
    RunContext duplicate();

    /**
     * Covers the current run context with another run context.
     *
     * @param anotherContext the run context to cover the current one with.
     */
    void cover(RunContext anotherContext);

    /**
     * @return the extra information associated with the run context.
     */
    Object getExtra();

    /**
     * clean up the run context.
     */
    default void clean() {
    }

    /**
     * Get the current run context of the specified class type.
     *
     * @param ctxClass the class type of the run context to retrieve.
     * @return the current run context of the specified class type, or null if not found.
     */
    static RunContext getCurrentCtx(Class<? extends RunContext> ctxClass) {
        return CONTEXT.get().get(ctxClass);
    }

    /**
     * Sets the current run context.
     *
     * @param context the run context to set as the current context.
     */
    static void setCurrentCtx(RunContext context) {
        CONTEXT.get().put(context.getClass(), context);
    }

    /**
     * Removes the current run context from the thread-local storage.
     */
    static void removeCurrentCtx() {
        CONTEXT.remove();
    }
}
