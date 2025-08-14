package top.huzz.jaksho.api.handler.creation;

import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import top.huzz.jaksho.api.CascadedRequestProvider;
import top.huzz.jaksho.api.context.CreateRunContext;
import top.huzz.jaksho.api.phase.CreatePhase;
import top.huzz.jaksho.common.able.DomainDescription;
import top.huzz.resilix.core.Phase;
import top.huzz.resilix.handler.PredictableRunHandler;
import top.huzz.resilix.predicate.HandlerRunPredicate;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

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

        // 如果createRequest是CascadedRequestProvider类型，则处理级联请求
        if (createRequest instanceof CascadedRequestProvider<?> cascadedRequestProvider) {
            List<?> objects = cascadedRequestProvider.cascadedRequests();
            if (CollectionUtils.isNotEmpty(objects)) {
                Supplier<C> cascadedCreatedObjectSupplier = context.getCascadedCreatedObjectSupplier();
                if (cascadedCreatedObjectSupplier == null) {
                    throw new IllegalArgumentException("cascadedCreatedObjectSupplier cannot be null when createRequest is a CascadedRequestProvider and objects are provided");
                }
                List<C> cascadedCreatedObjects = context.getCascadedCreatedObjects();
                for (Object object : objects) {
                    C c = cascadedCreatedObjectSupplier.get();
                    BeanUtils.copyProperties(object, c);
                    cascadedCreatedObjects.add(c);
                }
            }
        }
    }

    @Nonnull
    @Override
    public Phase phase() {
        return CreatePhase.TRANSFORM;
    }
}
