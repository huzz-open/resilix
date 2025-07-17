package org.huzz.resilix.api.run;

import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.huzz.resilix.api.run.handler.RunHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Default implementation of RunHandlerManager.
 *
 * @author chenji
 * @since 1.0.0
 */
public class DefaultRunHandlerManager extends AbstractRunHandlerManager {

    static final Map<Class<? extends Phase>, RunHandlerManager> managerCache = new ConcurrentHashMap<>();

    public DefaultRunHandlerManager(@Nonnull List<RunHandler<RunContext>> handlers) {
        super(handlers, null, null, null, null, null, null, null);
    }


    /**
     * Builds a RunHandlerManager for the specified phase class.
     *
     * @param phaseClass the class of the phase
     * @return an instance of AbstractRunHandlerManager for the specified phase
     */
    public static RunHandlerManager build(Class<? extends Phase> phaseClass) {
        RunHandlerManager manager = managerCache.get(phaseClass);
        if (manager == null) {
            synchronized (DefaultRunHandlerManager.class) {
                manager = managerCache.get(phaseClass);
                if (manager == null) {
                    List<RunHandler<RunContext>> runHandlers = RunHandlerCache.getRunHandlers(phaseClass);
                    manager = new DefaultRunHandlerManager(runHandlers);
                    managerCache.put(phaseClass, manager);
                }
            }
        }
        return manager;
    }

    @Configuration
    private static class RunHandlerCache {
        static final Map<Class<? extends Phase>, List<RunHandler<RunContext>>> runHandlersMap = new HashMap<>();

        @SuppressWarnings({"unchecked", "rawtypes"})
        RunHandlerCache(List<RunHandler<? extends RunContext>> runHandlers) {
            if (CollectionUtils.isNotEmpty(runHandlers)) {
                Map<Class<? extends Phase>, List<RunHandler<? extends RunContext>>> grouped = runHandlers.stream()
                        .collect(Collectors.groupingBy(runHandler -> runHandler.phase().getClass(), Collectors.toList()));

                Set<Map.Entry<Class<? extends Phase>, List<RunHandler<? extends RunContext>>>> entries = grouped.entrySet();
                for (Map.Entry<Class<? extends Phase>, List<RunHandler<? extends RunContext>>> entry : entries) {
                    List value = entry.getValue();
                    runHandlersMap.put(entry.getKey(), value);
                }
            }
        }

        @Nonnull
        static List<RunHandler<RunContext>> getRunHandlers(@Nonnull Class<? extends Phase> phaseClass) {
            Assert.notNull(phaseClass, "Phase class must not be null");
            return Optional.of(runHandlersMap.get(phaseClass)).orElseThrow(() -> new IllegalArgumentException("No run handlers found for phase：" + phaseClass.getSimpleName()));
        }
    }
}
