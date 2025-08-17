package top.huzz.resilix.validation;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import top.huzz.resilix.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author huzz
 * @since 1.0.2
 */
public final class ValidationContext {

    private final ConstraintValidatorContextImpl originalContext;
    private static final Method getCopyOfBasePathMethod;

    static {
        getCopyOfBasePathMethod = ReflectionUtils.getMethod(ConstraintValidatorContextImpl.class, "getCopyOfBasePath");
    }

    public ValidationContext(ConstraintValidatorContextImpl originalContext) {
        this.originalContext = originalContext;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends Annotation> ConstraintDescriptorImpl<T> getConstraintDescriptor() {
        ConstraintDescriptor<?> constraintDescriptor = originalContext.getConstraintDescriptor();
        if (constraintDescriptor instanceof ConstraintDescriptorImpl cdImpl) {
            return cdImpl;
        }
        throw new IllegalStateException("Expected ConstraintDescriptorImpl, but got: " + constraintDescriptor.getClass().getName());
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public <A extends Annotation> void executeCustomValidation(CustomValidationFunction<A> function) throws ConstraintViolationException {
        ConstraintDescriptor<?> constraintDescriptor = originalContext.getConstraintDescriptor();
        if (constraintDescriptor instanceof ConstraintDescriptorImpl cdImpl) {
            function.execute(cdImpl, pathImpl());
        } else {
            throw new IllegalStateException("Expected ConstraintDescriptorImpl, but got: " + constraintDescriptor.getClass().getName());
        }
    }

    private PathImpl pathImpl() {
        try {
            return (PathImpl) getCopyOfBasePathMethod.invoke(originalContext); // Ensure
        } catch (Exception e) {
            throw new RuntimeException("Failed to get copy of base path", e);
        }
    }


    /**
     * Custom validation function interface.
     *
     * @param <A> the type of the annotation used in the constraint
     */
    public interface CustomValidationFunction<A extends Annotation> {
        /**
         * Executes custom validation logic.
         *
         * @param constraintDescriptor the constraint descriptor
         * @param path                 the path for the validation
         * @throws ConstraintViolationException if the validation fails
         */
        void execute(ConstraintDescriptorImpl<A> constraintDescriptor, PathImpl path) throws ConstraintViolationException;
    }
}
