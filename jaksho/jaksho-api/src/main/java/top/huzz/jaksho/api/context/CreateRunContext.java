package top.huzz.jaksho.api.context;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import top.huzz.jaksho.common.able.BucketAble;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.resilix.core.SimpleRunContext;
import top.huzz.resilix.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @param <R> 创建请求的对象类型
 * @param <T> 数据库实体对象
 * @param <C> 级联创建的对象类型
 * @author chenji
 * @since 1.0.2
 */
@Getter
@Setter
public class CreateRunContext<R, T extends BasicProperties, C extends BasicProperties> extends SimpleRunContext {
    /**
     * 被创建后分配的ID
     */
    protected Integer id;
    /**
     * 创建请求
     */
    protected final R createRequest;

    /**
     * 被创建的对象
     */
    protected final T createdObject;
    /**
     * 被创建的对象的级联对象。有一些场景是在创建对象时需要同时创建一些级联对象，在数据库里面其实就是以外键或关联字段的形式存在的。
     */
    protected final List<C> cascadedCreatedObjects = new ArrayList<>();

    /**
     * 级联创建对象的供应商
     */
    protected Supplier<C> cascadedCreatedObjectSupplier;

    /**
     * 级联创建对象的ID设置器
     * 用于在创建对象后设置级联创建对象的ID
     */
    @NotNull
    protected BiConsumer<C, Integer> cascadedCreatedObjectIdSetter;

    /**
     * 创建对象前的消费者，与创建对象在事务中执行
     */
    protected Consumer<T> preCreatedObjectConsumer;

    /**
     * 创建对象后执行的消费者，与创建对象在同一个事务中执行
     */
    protected BiConsumer<BucketAble, T> createdObjectConsumer;


    public CreateRunContext<R, T, C> addCascadedCreatedObject(C cascadedCreatedObject) {
        this.cascadedCreatedObjects.add(cascadedCreatedObject);
        return this;
    }

    public CreateRunContext(R createRequest, Supplier<T> createdObjectSupplier, Supplier<C> cascadedCreatedObjectSupplier, BiConsumer<C, Integer> cascadedCreatedObjectIdSetter) {
        this(createRequest, null, createdObjectSupplier.get(), cascadedCreatedObjectSupplier, cascadedCreatedObjectIdSetter);
    }

    public CreateRunContext(R createRequest, Supplier<T> createdObjectSupplier) {
        this(createRequest, null, createdObjectSupplier.get(), null, null);
    }

    protected CreateRunContext(R createRequest,
                               @Nullable Class<T> createdClass, @Nullable T createdObject,
                               @Nullable Supplier<C> cascadedCreatedObjectSupplier,
                               @Nullable BiConsumer<C, Integer> cascadedCreatedObjectIdSetter) {
        if (createdClass == null && createdObject == null) {
            // 不能同时为空
            throw new IllegalArgumentException("createdClass and createdObject cannot both be null");
        }
        if (createdObject == null) {
            createdObject = ReflectionUtils.newInstance(createdClass);
        }

        this.createRequest = createRequest;
        this.createdObject = createdObject;
        this.cascadedCreatedObjectSupplier = cascadedCreatedObjectSupplier;
        this.cascadedCreatedObjectIdSetter = cascadedCreatedObjectIdSetter == null ? (c, id) -> {
        } : cascadedCreatedObjectIdSetter;
    }

}
