package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;

import java.lang.reflect.Method;

/**
 * @author huzz
 * @since 1.0.2
 */
public class FieldCapturingReflectivePropertyAccessor extends ReflectivePropertyAccessor {


    private static final ThreadLocal<EvaluationContext> contextThreadLocal = new ThreadLocal<>();

    @Override
    public boolean canRead(@Nonnull EvaluationContext context, Object target, @Nonnull String name) throws AccessException {
        contextThreadLocal.set(context);
        try {
            boolean canRead = super.canRead(context, target, name);
            if (canRead) {
                ValidationContext validationContext = Validations.getValidationContext();
                if (validationContext != null) {
                    validationContext.addCapturedField(name);
                }
            }
            return canRead;
        } finally {
            contextThreadLocal.remove();
        }
    }

    @Override
    protected Method findGetterForProperty(@Nonnull String propertyName, @Nonnull Class<?> clazz, boolean mustBeStatic) {
        Method getterForProperty = super.findGetterForProperty(propertyName, clazz, mustBeStatic);
        if (getterForProperty != null) {
            EvaluationContext evaluationContext = contextThreadLocal.get();
            if (evaluationContext instanceof StandardEvaluationContext ctx) {
                TypeLocator typeLocator = ctx.getTypeLocator();
                if (typeLocator instanceof StandardTypeLocator standardTypeLocator) {
                    Class<?> returnType = getterForProperty.getReturnType();
                    // Register the package of the return type to allow type resolution in SpEL expressions
                    standardTypeLocator.registerImport(returnType.getPackageName());
                }
            }

        }
        return getterForProperty;
    }
}
