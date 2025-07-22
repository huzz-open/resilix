package top.huzz.resilix.core;

import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import top.huzz.resilix.handler.RunHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * cache run handlers by phase class.
 *
 * @author chenji
 * @since 1.0.0
 */
public class RunHandlerCache {
    final Map<Class<? extends Phase>, List<RunHandler<RunContext>>> runHandlersMap = new HashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RunHandlerCache(List<RunHandler<? extends RunContext>> runHandlers) {
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
    public List<RunHandler<RunContext>> getRunHandlers(@Nonnull Class<? extends Phase> phaseClass) {
        Objects.requireNonNull(phaseClass, "Phase class must not be null");
        return Optional.of(runHandlersMap.get(phaseClass)).orElseThrow(() -> new IllegalArgumentException("No run handlers found for phase：" + phaseClass.getSimpleName()));
    }
}
