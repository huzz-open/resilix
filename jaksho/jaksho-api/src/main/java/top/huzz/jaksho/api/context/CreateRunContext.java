package top.huzz.jaksho.api.context;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import top.huzz.jaksho.common.able.DomainDescription;
import top.huzz.jaksho.common.able.Saver;
import top.huzz.resilix.core.SimpleRunContext;
import top.huzz.resilix.util.ReflectionUtils;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param <R> 创建请求的对象类型
 * @param <T> 数据库实体对象
 * @author chenji
 * @since 1.0.2
 */
@Getter
@Setter
public class CreateRunContext<R, T extends DomainDescription> extends SimpleRunContext {
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
     * 保存被创建的对象的函数
     */
    protected Saver<T, Integer> saver;


    public CreateRunContext(R createRequest, Class<T> createdClass) {
        this(createRequest, createdClass, null);
    }

    public CreateRunContext(R createRequest, Supplier<T> createdObjectSupplier) {
        this(createRequest, null, createdObjectSupplier.get());
    }

    public CreateRunContext(R createRequest, Function<R, T> createdObjectFunction) {
        this(createRequest, null, createdObjectFunction.apply(createRequest));
    }

    protected CreateRunContext(R createRequest, @Nullable Class<T> createdClass, @Nullable T createdObject) {
        if (createdClass == null && createdObject == null) {
            // 不能同时为空
            throw new IllegalArgumentException("createdClass and createdObject cannot both be null");
        }
        if (createdObject == null) {
            createdObject = ReflectionUtils.newInstance(createdClass);
        }

        this.createRequest = createRequest;
        this.createdObject = createdObject;
    }

}
