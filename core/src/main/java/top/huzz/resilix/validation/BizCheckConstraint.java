package top.huzz.resilix.validation;

import jakarta.validation.*;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import top.huzz.resilix.annotation.BizCheck;
import top.huzz.resilix.util.ApplicationContextUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author huzz
 * @since 1.0.2
 */
public class BizCheckConstraint implements ConstraintValidator<BizCheck, Object> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private static volatile BeanResolver beanResolver;
    private static volatile Validator validator;

    private Expression whenExpr;
    private boolean alwaysTrue;
    private Expression valueExpr;

    @Override
    public void initialize(BizCheck constraintAnnotation) {
        String whenExprStr = constraintAnnotation.when();
        if (BizCheck.ALWAYS_RUN_EXPR.equals(whenExprStr)) {
            this.alwaysTrue = true;
        } else {
            this.whenExpr = PARSER.parseExpression(whenExprStr);
        }
        this.valueExpr = PARSER.parseExpression(constraintAnnotation.value());

        initBeanResolver();
        initValidator();
    }

    @Override
    public boolean isValid(Object input, ConstraintValidatorContext context) {
        StandardEvaluationContext ctx = new StandardEvaluationContext(input);
        StandardTypeLocator typeLocator = new StandardTypeLocator();
        ctx.setTypeLocator(typeLocator);
        ctx.setBeanResolver(beanResolver);

        if (!needToCheck(ctx)) {
            return true;
        }
        try {
            ctx.registerFunction("__VALID",
                    BizCheckConstraint.class.getDeclaredMethod("valid", Object.class));
        } catch (Exception e) {
            throw new RuntimeException("Register __VALID failed", e);
        }
        try {
            Object result = valueExpr.getValue(ctx);
            return isValid(result);
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause() instanceof ConstraintViolationException cve) {
                // 把违例挂回到当前约束
                context.disableDefaultConstraintViolation();
                if (cve.getConstraintViolations() != null && !cve.getConstraintViolations().isEmpty()) {
                    for (ConstraintViolation<?> cv : cve.getConstraintViolations()) {
                        String msg = (cv.getPropertyPath() != null && !cv.getPropertyPath().toString().isEmpty())
                                ? cv.getPropertyPath() + " " + cv.getMessage()
                                : cv.getMessage();
                        context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
                    }
                } else {
                    // 只有消息没有集合时
                    context.buildConstraintViolationWithTemplate(
                                    e.getMessage() != null ? e.getMessage() : "Validation failed")
                            .addConstraintViolation();
                }
                return false;
            }
            // 任何其它异常也不要冒出去
            context.disableDefaultConstraintViolation();
            String msg = (e.getMessage() != null) ? e.getMessage() : e.getClass().getSimpleName();
            context.buildConstraintViolationWithTemplate("BizCheck error: " + msg).addConstraintViolation();
            return false;
        }
    }


    public static void valid(Object any) {
        if (any == null) {
            throw new ConstraintViolationException("Object to validate cannot be null", java.util.Collections.emptySet());
        }
        Validator v = validator;

        List<String> errors = new ArrayList<>();

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
                for (ConstraintViolation<?> cv : vs) {
                    errors.add(buildError(i, cv));
                }
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
                for (ConstraintViolation<?> cv : vs) {
                    errors.add(buildError(i, cv));
                }
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
                for (ConstraintViolation<?> cv : vs) {
                    errors.add(buildError(en.getKey(), cv));
                }
                result.addAll(vs);
            }
        } else {
            validateOne.accept(any);
        }

        if (!result.isEmpty()) {
            throw new ConstraintViolationException(String.join(",", errors), result);
        }
    }

    private static String buildError(Object key, ConstraintViolation<?> cv) {
        return "[" + key + "]." + cv.getPropertyPath() + " " + cv.getMessage();
    }

    private boolean isValid(Object result) {
        if (result == null) {
            // 没有返回值，只要不报异常，认为是通过校验
            return true;
        }
        if (result instanceof Boolean b) {
            // 如果返回值是布尔类型，直接返回
            return b;
        }
        // 执行到这里，说明没有抛出异常，也认为是通过校验
        return true;
    }

    private boolean needToCheck(StandardEvaluationContext ctx) {
        return alwaysTrue || (whenExpr != null && Boolean.TRUE.equals(whenExpr.getValue(ctx, Boolean.class)));
    }

    private void initBeanResolver() {
        if (beanResolver == null) {
            synchronized (BizCheckConstraint.class) {
                if (beanResolver == null) {
                    beanResolver = new BeanFactoryResolver(ApplicationContextUtils.getApplicationContext());
                }
            }
        }
    }

    private void initValidator() {
        if (validator == null) {
            synchronized (BizCheckConstraint.class) {
                if (validator == null) {
                    validator = ApplicationContextUtils.getBean(Validator.class);
                }
            }
        }
    }
}
