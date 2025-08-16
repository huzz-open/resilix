package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface DBUniqueCheckerProvider {
	/**
	 * Get a unique checker for the specified domain and fields.
	 *
	 * @param domainKey the key of the domain
	 * @param fields    the fields to check for uniqueness
	 * @return a DBUniqueChecker instance that checks the uniqueness of the specified fields and values
	 */
	@Nonnull
	DBUniqueChecker getChecker(String domainKey, String... fields);
}
