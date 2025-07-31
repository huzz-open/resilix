package top.huzz.jaksho.common.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenji
 * @since 1.0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CustomJSONType {
    String serializerMethod() default "toString";

    String deserializerMethod() default "of";

    boolean cacheable() default true;
}
