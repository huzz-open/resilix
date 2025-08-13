package top.huzz.resilix.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import top.huzz.resilix.validation.BizCheckConstraint;

import java.lang.annotation.*;

/**
 * Business validation annotation for performing business logic validation on fields or classes.
 * <p>
 * This annotation can be applied to either individual fields or entire classes, with different validation behaviors:
 * </p>
 * <ul>
 *   <li><strong>Field-level usage:</strong> When applied to a field, the validation typically focuses on that specific field
 *   and does not cross-reference other fields in the object. This is the most common usage pattern. Use <code>#this</code>
 *   to reference the current field value.</li>
 *   <li><strong>Class-level usage:</strong> When applied to a class using {@link List}, the validation can perform cross-field checks,
 *   allowing validation logic to examine and compare multiple fields within the same object. You can reference any field
 *   by its name directly.</li>
 * </ul>
 * <p>
 * The annotation uses Spring Expression Language (SpEL) to define validation logic, which can reference business check methods
 * marked with {@link BizCheckFunction}.
 * </p>
 * <p>
 * <strong>Usage Examples:</strong>
 * </p>
 * <pre><code>
 * // Field-level validation
 * public class UserRequest {
 *     &#64;BizCheck(value = "#this.length() > 0", message = "Name cannot be empty")
 *     private String name;
 *
 *     &#64;BizCheck(value = "#checkEmail(#this)", message = "Invalid email format")
 *     private String email;
 * }
 *
 * // Class-level validation with cross-field checks
 * &#64;BizCheck.List({
 *     &#64;BizCheck(when = "basicFieldType == BasicFieldType.OBJECT",
 *               value = "#__VALID(objectBizFieldTypeRefDTOList)"),
 *     &#64;BizCheck(when = "minimum != null && maximum != null",
 *               value = "minimum <= maximum",
 *               message = "Minimum must be less than or equal to maximum"),
 *     &#64;BizCheck(when = "basicFieldType == BasicFieldType.STRING",
 *               value = "name.length() > 0",
 *               message = "Name is required for string type")
 * })
 * public class CreateBizFieldTypeRequest {
 *     private String name;
 *     private BasicFieldType basicFieldType;
 *     private Integer minimum;
 *     private Integer maximum;
 *     private List<ObjectBizFieldTypeRefDTO> objectBizFieldTypeRefDTOList;
 * }
 * </code></pre>
 *
 * @author huzz
 * @see BizCheckFunction
 * @since 1.0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.FIELD})
@Constraint(validatedBy = {BizCheckConstraint.class})
@Repeatable(BizCheck.List.class)
public @interface BizCheck {
    /**
     * Constant for the expression that always evaluates to true.
     */
    String ALWAYS_RUN_EXPR = "true";

    /**
     * Defines the condition when this validation should be executed.
     * <p>
     * The validation will only run if this expression evaluates to true.
     * Defaults to "true", meaning the validation always runs.
     * </p>
     * <p>
     * Examples:
     * <ul>
     *   <li><code>"true"</code> - Always run (default)</li>
     *   <li><code>"basicFieldType == BasicFieldType.OBJECT"</code> - Run only when basicFieldType is OBJECT</li>
     *   <li><code>"minimum != null && maximum != null"</code> - Run only when both minimum and maximum are not null</li>
     * </ul>
     * </p>
     *
     * @return the conditional expression for when to run this validation
     */
    String when() default ALWAYS_RUN_EXPR;

    /**
     * The validation expression that defines the business logic to be executed.
     * <p>
     * This expression can reference business check methods marked with {@link BizCheckFunction}.
     * The expression language supports various operators and can access field values:
     * </p>
     * <ul>
     *   <li><code>#this</code> - References the current field value (field-level validation)</li>
     *   <li><code>fieldName</code> - References other fields (class-level validation)</li>
     *   <li><code>#__VALID(fieldName)</code> - Validates a field using its own validation annotations</li>
     *   <li><code>#methodName(parameter)</code> - Calls a business check method</li>
     * </ul>
     * <p>
     * Examples:
     * <ul>
     *   <li><code>"#this.length() > 0"</code> - Field is not empty</li>
     *   <li><code>"#minimum <= #maximum"</code> - Cross-field comparison</li>
     *   <li><code>"#checkEmail(#this)"</code> - Call business check method</li>
     *   <li><code>"#__VALID(objectList)"</code> - Validate nested objects</li>
     * </ul>
     * </p>
     *
     * @return the validation expression
     */
    String value();

    /**
     * The error message to display when validation fails.
     *
     * @return the validation error message
     */
    String message() default "{top.huzz.resilix.annotation.BizCheck.message}";

    /**
     * Groups for validation.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload for validation.
     *
     * @return the validation payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Container annotation for multiple {@link BizCheck} annotations.
     * <p>
     * This allows multiple business validations to be applied to the same element, particularly useful for
     * class-level validation where you need to perform multiple cross-field checks.
     * </p>
     * <p>
     * When using class-level validation, each {@link BizCheck} in the list can have different conditions
     * and validation logic, allowing for complex validation scenarios.
     * </p>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE_USE})
    @interface List {
        /**
         * An array of {@link BizCheck} annotations.
         *
         * @return the array of BizCheck annotations
         */
        BizCheck[] value();
    }
}
