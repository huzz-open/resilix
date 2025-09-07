package top.huzz.jaksho.api.cache;

import io.github.classgraph.ClassInfo;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import top.huzz.jaksho.common.able.Able;
import top.huzz.resilix.util.ClassUtils;
import top.huzz.resilix.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 能力类缓存初始化器，用于初始化能力类的缓存。通俗来讲，就是将实现了某种能力（由 {@link Able} 定义）的类按照能力类型进行缓存
 *
 * @param <A> 能力类型
 * @author huzz
 * @since 1.0.2
 */
@Slf4j
public abstract class AbleCacheInitializer<A extends Able> extends AbstractCacheInitializer {
    private static final String[] BASE_PACKAGE = new String[]{"top.huzz"};

    protected final Class<A> ableClass;
    protected final String[] packagesToScan;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public AbleCacheInitializer(Map cache, Class<A> ableClass, final String... packagesToScan) {
        super(cache);
        this.ableClass = ableClass;
        this.packagesToScan = (packagesToScan == null || packagesToScan.length == 0) ? BASE_PACKAGE : packagesToScan;
    }

    @Nonnull
    @Override
    protected Map<Object, Object> findAll() {
        Map<Object, Object> scanResult = new HashMap<>();
        Consumer<ClassInfo> classInfoHandler = classInfo -> {
            if (filter(classInfo)) {
                String name = classInfo.getName();
                Class<A> instanceAbleClass = ReflectionUtils.forName(name);
                A instance = ReflectionUtils.newInstance(instanceAbleClass);
                scanResult.put(buildKey(instance), buildValue(instance));
            }
        };
        if (ableClass.isInterface()) {
            ClassUtils.doWithClassImpl(ableClass, classInfoHandler, packagesToScan);
        } else {
            ClassUtils.doWithSubClass(ableClass, classInfoHandler, packagesToScan);
        }

        return scanResult;
    }

    /**
     * 过滤类信息，排除掉抽象类、接口、注解以及匿名内部类等不需要的类
     *
     * @param classInfo 类信息
     * @return true 如果类信息符合要求，false 如果类信息不符合要求
     */
    protected boolean filter(ClassInfo classInfo) {
        return !(classInfo.isAbstract() || classInfo.isInterfaceOrAnnotation() || classInfo.isAnonymousInnerClass());
    }

    /**
     * 构建缓存的键，默认使用类的Class对象作为键，子类可以重写此方法以实现自定义的键构建逻辑
     *
     * @param instance 能力类实例
     * @return 缓存键
     */
    protected Object buildKey(A instance) {
        return instance.getClass();
    }

    /**
     * 构建缓存的值，子类需要实现具体的构建逻辑
     *
     * @param instance 能力类实例
     * @return 缓存值
     */
    protected abstract Object buildValue(A instance);
}
