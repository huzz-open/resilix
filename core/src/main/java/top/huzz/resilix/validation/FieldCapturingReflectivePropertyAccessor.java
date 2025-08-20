package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;

/**
 * @author huzz
 * @since 1.0.2
 */
public class FieldCapturingReflectivePropertyAccessor extends ReflectivePropertyAccessor {

	@Override
	public boolean canRead(@Nonnull EvaluationContext context, Object target, @Nonnull String name) throws AccessException {
		ValidationContext validationContext = Validations.getValidationContext();
		if (validationContext != null) {
			validationContext.addCapturedField(name);
		}
		return super.canRead(context, target, name);
	}

}
