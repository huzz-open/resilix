package top.huzz.resilix.validation.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a business check function that can be used with the {@link BizCheck} annotation.
 * <p>
 * This annotation serves as an identifier for business validation methods. Any public method (including static methods)
 * can be used as a business check method. Adding this annotation helps remind other developers not to modify
 * the method signature arbitrarily when modifying the business logic.
 * </p>
 * <p>
 * The annotated method should contain business validation logic and can be referenced in {@link BizCheck}
 * annotations using the method's unique identifier. The method can be called from both field-level and class-level
 * {@link BizCheck} annotations.
 * </p>
 * <p>
 * <strong>Usage Examples:</strong>
 * </p>
 * <pre><code>
 * // Define business check methods
 * public class BizUtils {
 *     &#64;BizCheckFunction("checkCornSpecial")
 *     public static void checkCorn(String cron) throws InvalidCronExpressionException {
 *         if (StringUtils.isBlank(cron)) {
 *             return;
 *         }
 *         // validation logic...
 *     }
 *
 *     &#64;BizCheckFunction
 *     public static void checkEmail(String email) {
 *         if (!email.contains("@")) {
 *             throw new IllegalArgumentException("Invalid email format");
 *         }
 *     }
 * }
 *
 * // Field-level usage
 * public class DynamicVariableRequest {
 *     &#64;BizCheck(value = "#checkCornSpecial(#this)")
 *     private String cron;
 *
 *     &#64;BizCheck(value = "#checkEmail(#this)")
 *     private String email;
 * }
 *
 * // Class-level usage with cross-field validation
 * &#64;BizCheck.List({
 *     &#64;BizCheck(when = "basicFieldType == BasicFieldType.OBJECT",
 *               value = "#__VALID(objectBizFieldTypeRefDTOList)"),
 *     &#64;BizCheck(when = "basicFieldType == BasicFieldType.STRING",
 *               value = "#checkCornSpecial(name)")
 * })
 * public class CreateBizFieldTypeRequest {
 *     private String name;
 *     private BasicFieldType basicFieldType;
 *     private List<ObjectBizFieldTypeRefDTO> objectBizFieldTypeRefDTOList;
 * }
 *
 * // Alternative method reference formats (when no value is specified)
 * public class ValidationRequest {
 *     &#64;BizCheck(value = "#checkCorn(#this)")           // Using method name
 *     private String cron1;
 *
 *     &#64;BizCheck(value = "#BizUtils_checkCorn(#this)")  // Using class_methodName format
 *     private String cron2;
 * }
 * </code></pre>
 * <p>
 * <strong>Method Reference Formats:</strong>
 * <ul>
 *   <li><code>#customId(parameter)</code> - When value is specified in &#64;BizCheckFunction</li>
 *   <li><code>#methodName(parameter)</code> - Using method name directly</li>
 *   <li><code>#ClassName_methodName(parameter)</code> - Using class_methodName format</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Special Functions:</strong>
 * <ul>
 *   <li><code>#__VALID(fieldName)</code> - Validates a field using its own validation annotations</li>
 *   <li><code>#this</code> - References the current field value in field-level validation</li>
 *   <li><code>fieldName</code> - References other fields in class-level validation</li>
 * </ul>
 * </p>
 *
 * @author huzz
 * @see BizCheck
 * @since 1.0.2
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BizCheckFunction {
    /**
     * Returns the unique identifier for the method. This ID must be globally unique.
     * <p>
     * If specified, this value will be used as the method identifier in {@link BizCheck} expressions.
     * If not specified, the method can be referenced using either the method name or the
     * "ClassName_methodName" format.
     * </p>
     *
     * @return the unique identifier for the business check method, or empty string if not specified
     */
    String value() default "";
}
