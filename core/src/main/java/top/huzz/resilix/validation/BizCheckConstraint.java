package top.huzz.resilix.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import top.huzz.resilix.annotation.BizCheck;

/**
 * @author huzz
 * @since 1.0.2
 */
public class BizCheckConstraint implements ConstraintValidator<BizCheck, Object> {

    private String when;
    private String value;

    @Override
    public void initialize(BizCheck constraintAnnotation) {
        this.when = constraintAnnotation.when();
        this.value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        return false;
    }
}
