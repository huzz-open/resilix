package top.huzz.resilix.validation;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import top.huzz.resilix.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author huzz
 * @since 1.0.2
 */
public final class ValidationContext {

    private final ConstraintValidatorContextImpl originalContext;
    private final List<String> capturedFields = new ArrayList<>();
    private final Map<String, String> checkMapEntityFields = new HashMap<>();
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
    public <A extends Annotation> void executeCustomValidation(CustomValidationFunction<A> function, @Nullable String[] useToCheckFields) throws ConstraintViolationException {
        ConstraintDescriptor<?> constraintDescriptor = originalContext.getConstraintDescriptor();
        if (constraintDescriptor instanceof ConstraintDescriptorImpl cdImpl) {
            PathImpl nodes = pathImpl();
            String entityField = nodes.asString();
            String[] entityFields;
            if (useToCheckFields == null || useToCheckFields.length == 0) {
                if (StringUtils.isBlank(entityField)) {
                    // use captured fields if no specific fields are provided
                    if (capturedFields.isEmpty()) {
                        throw new ConstraintViolationException("No fields captured for validation", Collections.emptySet());
                    }
                    useToCheckFields = capturedFields.toArray(new String[0]);
                } else {
                    // If no specific fields are provided, use the entity field as the only field to check
                    useToCheckFields = new String[]{entityField};
                }
                entityFields = useToCheckFields;
            } else {
                if (StringUtils.isBlank(entityField)) {
                    if (capturedFields.size() != useToCheckFields.length) {
                        throw new ConstraintViolationException("Captured fields size does not match useToCheckFields size", Collections.emptySet());
                    }
                    entityFields = capturedFields.toArray(new String[0]);
                } else {
                    entityFields = new String[]{entityField};
                }
            }
            setUpCheckMapEntityFields(useToCheckFields, entityFields);
            function.execute(cdImpl, useToCheckFields, entityFields);
        } else {
            throw new IllegalStateException("Expected ConstraintDescriptorImpl, but got: " + constraintDescriptor.getClass().getName());
        }
    }

    /**
     * Retrieves the entity field corresponding to the specified useToCheckField.
     *
     * @param useToCheckField the field to check
     * @return the corresponding entity field, or null if the input is blank
     */
    public String getEntityField(String useToCheckField) {
        if (StringUtils.isBlank(useToCheckField)) {
            return null;
        }
        return checkMapEntityFields.getOrDefault(useToCheckField, useToCheckField);
    }

    /**
     * Retrieves the entity fields corresponding to the specified useToCheckFields.
     *
     * @param useToCheckFields the fields to check
     * @return an array of corresponding entity fields, or an empty array if input is null or empty
     */
    public String[] getEntityFields(String[] useToCheckFields) {
        if (useToCheckFields == null || useToCheckFields.length == 0) {
            return new String[0];
        }
        String[] entityFields = new String[useToCheckFields.length];
        for (int i = 0; i < useToCheckFields.length; i++) {
            entityFields[i] = getEntityField(useToCheckFields[i]);
        }
        return entityFields;
    }

    void addCapturedField(String field) {
        if (StringUtils.isNotBlank(field)) {
            capturedFields.add(field);
        }
    }

    private void setUpCheckMapEntityFields(String[] useToCheckFields, String[] entityFields) {
        if (useToCheckFields.length != entityFields.length) {
            throw new IllegalArgumentException("The length of useToCheckFields and entityFields must be the same.");
        }
        for (int i = 0; i < useToCheckFields.length; i++) {
            String useField = useToCheckFields[i];
            String entityField = entityFields[i];
            checkMapEntityFields.put(useField, entityField);
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
         * @param useToCheckFields     the fields to check
         * @param entityFields         the path of the entity
         * @throws ConstraintViolationException if the validation fails
         */
        void execute(ConstraintDescriptorImpl<A> constraintDescriptor, final String[] useToCheckFields, final String[] entityFields) throws ConstraintViolationException;
    }
}
