package top.huzz.jaksho.api.handler.creation;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.common.able.DomainDescription;
import top.huzz.jaksho.common.able.Saver;
import top.huzz.resilix.core.Phase;
import top.huzz.resilix.handler.PredictableRunHandler;
import top.huzz.resilix.predicate.HandlerRunPredicate;

import java.util.List;
import java.util.Objects;

/**
 * @author chenji
 * @since 1.0.2
 */
@Component
public class SaveRunHandler<R, T extends DomainDescription> extends PredictableRunHandler<CreateRunContext<R, T>> {

    protected SaveRunHandler(List<HandlerRunPredicate<CreateRunContext<R, T>>> handlerRunPredicates) {
        super(handlerRunPredicates);
    }

    @Override
    public void handle(CreateRunContext<R, T> context) throws Exception {
        Saver<T, Integer> saver = context.getSaver();
        Objects.requireNonNull(saver);
        T createdObject = context.getCreatedObject();
        Integer id = saver.save(createdObject);
        context.setId(id);
    }

    @Nonnull
    @Override
    public Phase phase() {
        return CreatePhase.SAVE;
    }
}
