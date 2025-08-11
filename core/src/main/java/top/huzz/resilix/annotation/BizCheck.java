package top.huzz.resilix.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import top.huzz.resilix.validation.BizCheckConstraint;

import java.lang.annotation.*;

/**
 * @author huzz
 * @since 1.0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
@Constraint(validatedBy = {BizCheckConstraint.class})
@Repeatable(BizCheck.List.class)
public @interface BizCheck {
    String DEFAULT_WHEN = "true";

    String when() default DEFAULT_WHEN;

    String value();

    String message() default "业务校验失败";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE_USE})
    @interface List {
        BizCheck[] value();
    }
}
