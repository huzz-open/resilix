package top.huzz.resilix.validation;

import com.google.common.collect.Lists;
import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.metadata.ConstraintDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import top.huzz.resilix.util.ReflectionUtils;
import top.huzz.resilix.validation.checker.Checker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author huzz
 * @since 1.0.2
 */
@Slf4j
public final class ValidationContext {

    private final ConstraintValidatorContextImpl originalContext;
    private final List<String> capturedFields = new ArrayList<>();
    private final Map<String, String> checkMapEntityFields = new HashMap<>();
    private final List<LeadingFieldValueProvider> leadingFieldValueProviders;

    private static final Method getCopyOfBasePathMethod;


    static {
        getCopyOfBasePathMethod = ReflectionUtils.getMethod(ConstraintValidatorContextImpl.class, "getCopyOfBasePath");
    }

    public ValidationContext(ConstraintValidatorContextImpl originalContext, @Nullable List<LeadingFieldValueProvider> leadingFieldValueProviders) {
        this.originalContext = originalContext;
        this.leadingFieldValueProviders = leadingFieldValueProviders;
    }

    public ValidationContext(ConstraintValidatorContextImpl originalContext) {
        this(originalContext, null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends Annotation> ConstraintDescriptorImpl<T> getConstraintDescriptor() {
        ConstraintDescriptor<?> constraintDescriptor = originalContext.getConstraintDescriptor();
        if (constraintDescriptor instanceof ConstraintDescriptorImpl cdImpl) {
            return cdImpl;
        }
        throw new IllegalStateException("Expected ConstraintDescriptorImpl, but got: " + constraintDescriptor.getClass().getName());
    }

    /**
     * Executes custom validation logic using the provided function.
     *
     * @param function         the custom validation function to execute
     * @param type             the type of checker
     * @param useToCheckFields the fields to check; if any leading providers are present, their fields are placed at the beginning
     * @param checkValues      the values to check; if any leading providers are present, their values are placed at the beginning
     * @param <A>              the type of the annotation used in the constraint
     * @see LeadingFieldValueProvider
     */
    public <A extends Annotation> void executeCustomValidation(CustomValidationFunction<A> function,
                                                               Checker.Type type,
                                                               @Nullable final String[] useToCheckFields,
                                                               Object[] checkValues) throws ConstraintViolationException {
        ConstraintDescriptorImpl<A> cdImpl = getConstraintDescriptor();

        // checked fields
        List<String> cfs = new ArrayList<>();
        // entity fields
        List<String> efs = new ArrayList<>();
        // check values
        List<Object> cvs = Lists.newArrayList(checkValues);

        PathImpl nodes = pathImpl();
        String entityField = nodes.asString();
        if (useToCheckFields == null || useToCheckFields.length == 0) {
            if (StringUtils.isBlank(entityField)) {
                // use captured fields if no specific fields are provided
                if (capturedFields.isEmpty()) {
                    throw new ConstraintViolationException("No fields captured for validation", Collections.emptySet());
                }
                cfs.addAll(capturedFields);
                efs.addAll(capturedFields);
            } else {
                // If no specific fields are provided, use the entity field as the only field to check
                cfs.add(entityField);
                efs.add(entityField);
            }
        } else {
            cfs.addAll(Arrays.asList(useToCheckFields));
            if (StringUtils.isBlank(entityField)) {
                if (capturedFields.size() != useToCheckFields.length) {
                    throw new ConstraintViolationException("Captured fields size does not match useToCheckFields size", Collections.emptySet());
                }
                efs.addAll(capturedFields);
            } else {
                efs.add(entityField);
            }
        }
        LeadingFieldValueProvider.PredicateInput predicateInput = new LeadingFieldValueProvider.PredicateInput(type, cfs, efs, cvs);
        // add leading fields at the front
        prependLeadingFieldValues(predicateInput, cfs, efs, cvs);
        // set up the mapping between useToCheckFields and entityFields
        setUpCheckMapEntityFields(cfs, efs);
        // Execute the custom validation function
        function.execute(cdImpl, cfs.toArray(new String[0]), efs.toArray(new String[0]), cvs.toArray());
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

    private void prependLeadingFieldValues(LeadingFieldValueProvider.PredicateInput predicateInput, List<String> cfs, List<String> efs, List<Object> cvs) {
        if (leadingFieldValueProviders == null) {
            return;
        }

        List<String> leadingCfs = new ArrayList<>();
        List<String> leadingEfs = new ArrayList<>();
        List<Object> leadingCvs = new ArrayList<>();

        for (LeadingFieldValueProvider leadingFieldValueProvider : leadingFieldValueProviders) {
            boolean shouldAsLeading = leadingFieldValueProvider.shouldAsLeading(predicateInput);
            if (!shouldAsLeading) {
                continue;
            }
            LeadingFieldValue leadingFieldValue = leadingFieldValueProvider.get();
            String checkField = leadingFieldValue.getCheckField();
            String entityFieldValue = leadingFieldValue.getEntityField();
            Object value = leadingFieldValue.getValue();
            if (StringUtils.isAnyBlank(checkField, entityFieldValue) || value == null) {
                log.warn("Leading field value provider returned invalid data, do not include: checkField={}, entityField={}, value={}",
                        checkField, entityFieldValue, value);
                continue;
            }
            leadingCfs.add(checkField);
            leadingEfs.add(entityFieldValue);
            leadingCvs.add(value);
        }

        if (!leadingCfs.isEmpty()) {
            cfs.addAll(0, leadingCfs);
            efs.addAll(0, leadingEfs);
            cvs.addAll(0, leadingCvs);
        }
    }

    private String buildLeadingFieldKey(String checkField, String entityField) {
        return checkField + "-" + entityField;
    }

    private void setUpCheckMapEntityFields(List<String> useToCheckFields, List<String> entityFields) {
        if (useToCheckFields.size() != entityFields.size()) {
            throw new IllegalArgumentException("The length of useToCheckFields and entityFields must be the same.");
        }
        for (int i = 0, len = useToCheckFields.size(); i < len; i++) {
            String useField = useToCheckFields.get(i);
            String entityField = entityFields.get(i);
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
         * @param checkValues          the values to check
         * @throws ConstraintViolationException if the validation fails
         */
        void execute(ConstraintDescriptorImpl<A> constraintDescriptor, final String[] useToCheckFields, final String[] entityFields, Object[] checkValues) throws ConstraintViolationException;
    }
}
