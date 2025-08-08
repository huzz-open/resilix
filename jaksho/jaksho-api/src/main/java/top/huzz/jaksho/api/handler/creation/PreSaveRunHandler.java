package top.huzz.jaksho.api.handler.creation;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.common.able.DomainDescription;
import top.huzz.jaksho.common.able.Saver;
import top.huzz.jaksho.common.able.SaverBuilder;
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
@Slf4j
public class PreSaveRunHandler<R, T extends DomainDescription> extends PredictableRunHandler<CreateRunContext<R, T>> {

    @Resource
    private SaverBuilder<T, Integer> saverBuilder;

    protected PreSaveRunHandler(List<HandlerRunPredicate<CreateRunContext<R, T>>> handlerRunPredicates) {
        super(handlerRunPredicates);
    }

    @Override
    public void handle(CreateRunContext<R, T> context) throws Exception {
        T createdObject = context.getCreatedObject();
        Objects.requireNonNull(createdObject);
        Saver<T, Integer> saver = saverBuilder.getSaver(createdObject);
        Objects.requireNonNull(saver, "Saver cannot be null for object: " + createdObject.getClass().getName());
        log.info("Using saver: {} for object: {}", saver.getClass().getName(), createdObject.getClass().getName());
        context.setSaver(saver);
    }

    @Nonnull
    @Override
    public Phase phase() {
        return CreatePhase.PRE_SAVE;
    }
}
