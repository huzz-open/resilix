package top.huzz.resilix.validation;

import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import top.huzz.resilix.annotation.BizCheckFunction;
import top.huzz.resilix.util.ApplicationContextUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author chenji
 * @since 1.0.0
 */
@Slf4j
public class Validations {

    private static volatile Validator validator;

    @BizCheckFunction("__VALID")
    private static void valid(Object any) {
        if (any == null) {
            throw new ConstraintViolationException("Object to validate cannot be null", java.util.Collections.emptySet());
        }
        Validator v = initValidator();

        Set<ConstraintViolation<Object>> result = new LinkedHashSet<>();

        Consumer<Object> validateOne = o -> {
            if (o == null) return;
            Set<ConstraintViolation<Object>> vs = v.validate(o);
            result.addAll(vs);
        };

        if (any instanceof Iterable<?> it) {
            int i = 0;
            for (Object e : it) {
                Set<ConstraintViolation<Object>> vs = v.validate(e);
                result.addAll(vs);
                i++;
            }
            if (i == 0) {
                throw new ConstraintViolationException("Iterable to validate cannot be empty", Collections.emptySet());
            }
        } else if (any.getClass().isArray()) {
            int n = java.lang.reflect.Array.getLength(any);
            if (n == 0) {
                throw new ConstraintViolationException("Array to validate cannot be empty", Collections.emptySet());
            }
            for (int i = 0; i < n; i++) {
                Object e = java.lang.reflect.Array.get(any, i);
                Set<ConstraintViolation<Object>> vs = v.validate(e);
                result.addAll(vs);
            }
        } else if (any instanceof java.util.Optional<?> opt) {
            opt.ifPresent(validateOne);
        } else if (any instanceof java.util.Map<?, ?> m) {
            if (m.isEmpty()) {
                throw new ConstraintViolationException("Map to validate cannot be empty", Collections.emptySet());
            }
            for (var en : m.entrySet()) {
                Set<ConstraintViolation<Object>> vs = v.validate(en.getValue());
                result.addAll(vs);
            }
        } else {
            validateOne.accept(any);
        }

        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
    }

    private static Validator initValidator() {
        if (validator == null) {
            synchronized (BizCheckConstraint.class) {
                if (validator == null) {
                    validator = ApplicationContextUtils.getBean(Validator.class);
                    if (validator == null) {
                        log.info("No Validator bean found in application context, creating default Validator");
                        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                            validator = factory.getValidator();
                        }
                    }
                }
            }
        }
        return validator;
    }

}
