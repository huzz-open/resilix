package top.huzz.jaksho.api.handler.creation;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.common.able.DomainDescription;
import top.huzz.resilix.core.Phase;
import top.huzz.resilix.handler.PredictableRunHandler;
import top.huzz.resilix.predicate.HandlerRunPredicate;

import java.util.List;

/**
 * @author chenji
 * @since 1.0.2
 */
@Component
public class PostCreateRunHandler<R, T extends DomainDescription, C extends DomainDescription> extends PredictableRunHandler<CreateRunContext<R, T, C>> {

    protected PostCreateRunHandler(List<HandlerRunPredicate<CreateRunContext<R, T, C>>> handlerRunPredicates) {
        super(handlerRunPredicates);
    }

    @Override
    public void handle(CreateRunContext<R, T, C> context) throws Exception {

    }

    @Nonnull
    @Override
    public Phase phase() {
        return CreatePhase.POST_PROCESS;
    }
}
