package top.huzz.resilix.core;

import jakarta.annotation.Nonnull;
import top.huzz.resilix.handler.RunHandler;

import java.util.List;

/**
 * Default implementation of RunHandlerManager.
 *
 * @author chenji
 * @since 1.0.0
 */
public class DefaultRunHandlerManager extends AbstractRunHandlerManager {
    public DefaultRunHandlerManager(@Nonnull List<RunHandler<RunContext>> handlers) {
        super(handlers, null, null, null, null, null, null, null);
    }
}
