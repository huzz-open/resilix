package top.huzz.resilix.validation.checker;

import jakarta.validation.ConstraintViolationException;
import top.huzz.resilix.core.StringGroupable;

/**
 * Interface for a checker that validates multiple values.
 *
 * @author huzz
 * @since 1.0.2
 */
public interface Checker extends StringGroupable {
	/**
	 * Checks the validity of the provided values.
	 *
	 * @param values the values to check
	 * @throws ConstraintViolationException if the values violate any constraints
	 */
	void check(Object... values) throws ConstraintViolationException;
}
