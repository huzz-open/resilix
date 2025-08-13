package top.huzz.resilix.evaluation;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import top.huzz.resilix.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * finds reducible methods using any specified annotation type.
 *
 * @param <A> an annotation type that is used to find reducible methods
 * @author chenji
 * @since 1.0.0
 */
@Slf4j
public abstract class AnnotationReducibleMethodProvider<A extends Annotation> extends AbstractReducibleMethodProvider {
    protected final Class<A> annotationType;
    protected final List<String> scanPackages = new ArrayList<>();

    protected AnnotationReducibleMethodProvider(Class<A> annotationType, String... scanPackages) {
        this.annotationType = annotationType;
        if (scanPackages != null) {
            for (String scanPackage : scanPackages) {
                if (scanPackage != null && !scanPackage.isBlank()) {
                    this.scanPackages.add(scanPackage);
                }
            }
        }
        String[] defaultScanPackages = ReflectionUtils.DEFAULT_SCAN_PACKAGES;
        if (this.scanPackages.isEmpty()) {
            this.scanPackages.addAll(Arrays.asList(defaultScanPackages));
            log.info("No scan packages provided for annotation {}, using default scan packages: {}",
                    annotationType.getSimpleName(), defaultScanPackages);
        } else {
            log.info("Using scan packages for annotation {}: {}",
                    annotationType.getSimpleName(), this.scanPackages);
        }
    }

    @Override
    protected void initialize() {
        Set<Method> methodsAll = ReflectionUtils.reflectionsScanMethods(annotationType, scanPackages.toArray(new String[]{}));
        String simpleName = annotationType.getSimpleName();
        if (methodsAll.isEmpty()) {
            log.warn("No methods found with annotation {} in packages: {}", simpleName, scanPackages);
        } else {
            log.info("Found {} methods with annotation {} in packages: {}", methodsAll.size(), simpleName, scanPackages);
            for (Method method : methodsAll) {
                Class<?> declaringClass = method.getDeclaringClass();
                A annotation = method.getAnnotation(annotationType);
                Objects.requireNonNull(annotation);
                Set<String> reducibleMethodNames = name(annotation, declaringClass, method);
                if (reducibleMethodNames.isEmpty()) {
                    log.warn("No reducible method names found for method: {} with annotation: {} in class: {}",
                            method.getName(), simpleName, declaringClass.getName());
                    continue;
                }
                for (String reducibleMethodName : reducibleMethodNames) {
                    log.info("Found reducible method: {} with annotation {} in class: {}",
                            reducibleMethodName, simpleName, declaringClass.getName());
                    addMethod(reducibleMethodName, method);
                }
            }
        }
    }

    /**
     * Get reducible method name from the annotation.
     *
     * @param annotation     the annotation instance
     * @param declaringClass the class that declares the method
     * @param method         the method annotated with the annotation
     * @return name of the reducible method, which can be a single name or a set of names
     */
    @Nonnull
    protected abstract Set<String> name(A annotation, Class<?> declaringClass, Method method);
}
