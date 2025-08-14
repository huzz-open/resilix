package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import top.huzz.resilix.annotation.BizCheckFunction;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author chenji
 * @since 1.0.0
 */
public class DefaultAnnotationReducibleMethodProvider extends AnnotationReducibleMethodProvider<BizCheckFunction> {

    public DefaultAnnotationReducibleMethodProvider(Class<BizCheckFunction> annotationType, String... scanPackages) {
        super(annotationType, scanPackages);
    }

    @Nonnull
    @Override
    protected Set<String> name(BizCheckFunction annotation, Class<?> declaringClass, Method method) {
        String value = annotation.value();
        if (StringUtils.isNotEmpty(value)) {
            return Set.of(value);
        }
        return defaultName(declaringClass, method);
    }

    protected Set<String> defaultName(Class<?> declaringClass, Method method) {
        String name = method.getName();
        // use class name append method name as alias name
        return Set.of(name, declaringClass.getSimpleName() + "_" + name);
    }
}
