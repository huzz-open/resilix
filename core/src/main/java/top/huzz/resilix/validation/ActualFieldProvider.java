package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface ActualFieldProvider {
	/**
	 * Get the actual field name based on the alias field.
	 *
	 * @param aliasField the alias field name to query
	 * @return the actual field name corresponding to the alias field
	 */
	@Nonnull
	String actual(@Nonnull String aliasField);

	/**
	 * Map an alias field to its actual field name.
	 *
	 * @param aliasField  the alias field name
	 * @param actualField the actual field name
	 */
	void mapping(@Nonnull String aliasField, @Nonnull String actualField);

}
