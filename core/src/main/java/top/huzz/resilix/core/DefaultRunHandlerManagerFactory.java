package top.huzz.resilix.core;

import top.huzz.resilix.handler.RunHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of RunHandlerManagerFactory that provides a singleton instance of RunHandlerManager for each phase.
 *
 * @author chenji
 * @since 1.0.0
 */
public final class DefaultRunHandlerManagerFactory implements RunHandlerManagerFactory {

    final RunHandlerCache runHandlerCache;

    /**
     * Cache to hold RunHandlerManager instances for each phase class.
     * This is a thread-safe cache that ensures only one instance of RunHandlerManager is created for each phase class.
     */
    static final Map<Class<? extends Phase>, RunHandlerManager> managerCache = new ConcurrentHashMap<>();

    public DefaultRunHandlerManagerFactory(RunHandlerCache runHandlerCache) {
        Objects.requireNonNull(runHandlerCache, "RunHandlerCache must not be null");
        this.runHandlerCache = runHandlerCache;
    }

    /**
     * Builds a RunHandlerManager for the specified phase class.
     *
     * @param phaseClass the class of the phase
     * @return an instance of AbstractRunHandlerManager for the specified phase
     */
    @Override
    public RunHandlerManager build(Class<? extends Phase> phaseClass) {
        RunHandlerManager manager = managerCache.get(phaseClass);
        if (manager == null) {
            synchronized (DefaultRunHandlerManager.class) {
                manager = managerCache.get(phaseClass);
                if (manager == null) {
                    List<RunHandler<RunContext>> runHandlers = runHandlerCache.getRunHandlers(phaseClass);
                    manager = new DefaultRunHandlerManager(runHandlers);
                    managerCache.put(phaseClass, manager);
                }
            }
        }
        return manager;
    }
}
