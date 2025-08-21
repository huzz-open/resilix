package top.huzz.resilix.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import top.huzz.resilix.util.ApplicationContextUtils;
import top.huzz.resilix.validation.annotation.BizCheck;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author huzz
 * @since 1.0.2
 */
public class BizCheckConstraint implements ConstraintValidator<BizCheck, Object> {

	private static final SpelExpressionParser PARSER = new SpelExpressionParser();
	private static volatile BeanResolver beanResolver;
	private static volatile List<ExpressionConverter> expressionConverters;

	private Expression whenExpr;
	private boolean alwaysTrue;
	private Expression valueExpr;
	private BizCheck bizCheck;

	@Override
	public void initialize(BizCheck constraintAnnotation) {
		this.bizCheck = constraintAnnotation;
		initBeanResolver();
		initExpressionConverters();


		String whenExprStr = constraintAnnotation.when();
		if (BizCheck.ALWAYS_RUN_EXPR.equals(whenExprStr)) {
			this.alwaysTrue = true;
		} else {
			this.whenExpr = PARSER.parseExpression(beforeParseExpression(whenExprStr));
		}
		this.valueExpr = PARSER.parseExpression(beforeParseExpression(constraintAnnotation.value()));
	}

	private String beforeParseExpression(String expr) {
		for (ExpressionConverter expressionConverter : expressionConverters) {
			if (expressionConverter != null) {
				expr = expressionConverter.convert(expr, bizCheck);
			}
		}
		return expr;
	}

	@Override
	public boolean isValid(Object input, ConstraintValidatorContext context) {
		try {
			if (context instanceof ConstraintValidatorContextImpl ctxImpl) {
				Validations.setValidationContext(new ValidationContext(ctxImpl));
			}
			return doValid(input, context);
		} finally {
			Validations.clearValidationContext();
		}
	}

	private boolean doValid(Object input, ConstraintValidatorContext context) {
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
		Throwable cause = e.getCause();
		if (cause instanceof ConstraintViolationException cve) {
			handleException(context, cve);
		} else if (e instanceof ConstraintViolationException cve) {
			handleException(context, cve);
		} else {
			// 任何其它异常也不要冒出去
			context.disableDefaultConstraintViolation();
			String msg = (e.getMessage() != null) ? e.getMessage() : e.getClass().getSimpleName();
			context.buildConstraintViolationWithTemplate("BizCheck error: " + msg).addConstraintViolation();
		}
		return false;
	}

	private void handleException(ConstraintValidatorContext context, ConstraintViolationException cve) {
		String message = cve.getMessage();
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
			String errMsg = (message == null || message.isBlank()) ? "Validation failed" : message;
			context.buildConstraintViolationWithTemplate(errMsg).addConstraintViolation();
		}
	}

	private StandardEvaluationContext buildEvaluationContext(Object input) {
		StandardEvaluationContext ctx = new StandardEvaluationContext(input);
		StandardTypeLocator typeLocator = new StandardTypeLocator();
		ctx.setTypeLocator(typeLocator);
		ctx.setBeanResolver(beanResolver);
		ArrayList<PropertyAccessor> propertyAccessors = new ArrayList<>();
		propertyAccessors.add(new FieldCapturingReflectivePropertyAccessor());
		ctx.setPropertyAccessors(propertyAccessors);
		return ctx;
	}

	private void registerFunctions(StandardEvaluationContext ctx) {
		Map<String, Method> functions = Validations.getFunctions();
		functions.forEach(ctx::registerFunction);
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

	private static void initExpressionConverters() {
		if (expressionConverters == null) {
			synchronized (BizCheckConstraint.class) {
				if (expressionConverters == null) {
					expressionConverters = ApplicationContextUtils.listBean(ExpressionConverter.class);
				}
			}
		}
	}
}
