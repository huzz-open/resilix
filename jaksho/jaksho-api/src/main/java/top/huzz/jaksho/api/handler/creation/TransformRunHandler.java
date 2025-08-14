package top.huzz.jaksho.api.handler.creation;

import jakarta.annotation.Nonnull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.common.able.DomainDescription;
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
public class TransformRunHandler<R, T extends DomainDescription, C extends DomainDescription> extends PredictableRunHandler<CreateRunContext<R, T, C>> {

    protected TransformRunHandler(List<HandlerRunPredicate<CreateRunContext<R, T, C>>> handlerRunPredicates) {
        super(handlerRunPredicates);
    }

    @Override
    public void handle(CreateRunContext<R, T, C> context) throws Exception {
        Object createRequest = context.getCreateRequest();
        Objects.requireNonNull(createRequest);

        Object createdObject = context.getCreatedObject();
        Objects.requireNonNull(createdObject);

        // 这里暂时使用copyProperties，后续可以考虑使用更快的setter方法
        BeanUtils.copyProperties(createRequest, createdObject);
    }

    @Nonnull
    @Override
    public Phase phase() {
        return CreatePhase.TRANSFORM;
    }
}
