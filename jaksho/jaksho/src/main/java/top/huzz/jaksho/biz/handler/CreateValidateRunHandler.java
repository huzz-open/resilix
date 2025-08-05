package top.huzz.jaksho.biz.handler;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import top.huzz.jaksho.biz.context.CreateRunContext;
import top.huzz.jaksho.biz.phase.CreatePhase;
import top.huzz.resilix.core.Phase;
import top.huzz.resilix.handler.RunHandler;

/**
 * @author chenji
 * @since 1.0.0
 */
@Component
public class CreateValidateRunHandler implements RunHandler<CreateRunContext> {
    @Override
    public void handle(CreateRunContext context) throws Exception {

    }

    @Nonnull
    @Override
    public Phase phase() {
        return CreatePhase.VALIDATE;
    }
}
