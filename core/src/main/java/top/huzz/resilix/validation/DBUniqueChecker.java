package top.huzz.resilix.validation;

import top.huzz.resilix.core.StringGroupable;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface DBUniqueChecker extends StringGroupable {
	/**
	 * Checks if a value exists in the database.
	 * <p>
	 * This method is used to validate that a given value is unique in the database.
	 * It can be used in conjunction with validation annotations to ensure that
	 * certain fields do not contain duplicate values.
	 * </p>
	 *
	 * @param values the value to check for uniqueness
	 * @return true if the value exists in the database, false otherwise
	 */
	boolean exists(Object... values);
}
