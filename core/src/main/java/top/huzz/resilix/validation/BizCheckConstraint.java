package top.huzz.resilix.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import top.huzz.resilix.annotation.BizCheck;
import top.huzz.resilix.util.ApplicationContextUtils;

import java.util.List;

/**
 * @author huzz
 * @since 1.0.2
 */
public class BizCheckConstraint implements ConstraintValidator<BizCheck, Object> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private static volatile BeanResolver beanResolver;
    private static volatile List<ReducibleMethodProvider> reducibleMethodProviders;

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
        initReducibleMethodProviders();
    }

    @Override
    public boolean isValid(Object input, ConstraintValidatorContext context) {
        StandardEvaluationContext ctx = buildEvaluationContext(input);

        if (!needToCheck(ctx)) {
            return true;
        }

        registerFunctions(ctx);

        try {
            Object result = valueExpr.getValue(ctx);
            return isValid(result);
        } catch (Exception e) {
            return handleException(context, e);
        }
    }

    private boolean handleException(ConstraintValidatorContext context, Exception e) {
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
        } else {
            // 任何其它异常也不要冒出去
            context.disableDefaultConstraintViolation();
            String msg = (e.getMessage() != null) ? e.getMessage() : e.getClass().getSimpleName();
            context.buildConstraintViolationWithTemplate("BizCheck error: " + msg).addConstraintViolation();
        }
        return false;
    }

    private StandardEvaluationContext buildEvaluationContext(Object input) {
        StandardEvaluationContext ctx = new StandardEvaluationContext(input);
        StandardTypeLocator typeLocator = new StandardTypeLocator();
        ctx.setTypeLocator(typeLocator);
        ctx.setBeanResolver(beanResolver);
        return ctx;
    }

    private void registerFunctions(StandardEvaluationContext ctx) {
        reducibleMethodProviders.stream().map(ReducibleMethodProvider::methods).forEach(methods ->
                methods.forEach(ctx::registerFunction)
        );
    }

    private static boolean isValid(Object result) {
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

    private static void initBeanResolver() {
        if (beanResolver == null) {
            synchronized (BizCheckConstraint.class) {
                if (beanResolver == null) {
                    beanResolver = new BeanFactoryResolver(ApplicationContextUtils.getApplicationContext());
                }
            }
        }
    }

    private static void initReducibleMethodProviders() {
        if (reducibleMethodProviders == null) {
            synchronized (BizCheckConstraint.class) {
                if (reducibleMethodProviders == null) {
                    reducibleMethodProviders = ApplicationContextUtils.listBean(ReducibleMethodProvider.class);
                }
            }
        }
    }
}
