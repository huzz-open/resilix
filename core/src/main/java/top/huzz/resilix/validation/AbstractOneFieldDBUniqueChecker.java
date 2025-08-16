package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;

/**
 * @author huzz
 * @since 1.0.2
 */
public abstract class AbstractOneFieldDBUniqueChecker extends AbstractDBUniqueChecker {
	protected AbstractOneFieldDBUniqueChecker(String domainKey, @Nonnull String field) {
		super(domainKey, field);
	}

	@Override
	protected boolean exists(String domainKey, String[] fields, Object[] values) {
		return exists(domainKey, fields[0], values[0]);
	}

	/**
	 * Checks if a value exists in the database.
	 * <p>
	 * This method is used to validate that a given value is unique in the database.
	 * It can be used in conjunction with validation annotations to ensure that
	 * certain fields do not contain duplicate values.
	 * </p>
	 *
	 * @param domainKey the domain key to identify the context
	 * @param field     the field corresponding to the value
	 * @param value     the value to check for uniqueness
	 * @return true if the value exists in the database, false otherwise
	 */
	protected abstract boolean exists(String domainKey, String field, Object value);
}
