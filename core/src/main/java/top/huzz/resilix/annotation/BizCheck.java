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
    String ALWAYS_RUN_EXPR = "true";

    String when() default ALWAYS_RUN_EXPR;

    String value();

    String message() default "业务校验失败";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

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
