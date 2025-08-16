package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author huzz
 * @since 1.0.2
 */
public abstract class AbstractDBUniqueChecker implements DBUniqueChecker {

	protected final String domainKey;
	protected final String[] fields;

	protected AbstractDBUniqueChecker(String domainKey, @Nonnull String... fields) {
		if (domainKey == null || domainKey.isEmpty()) {
			throw new IllegalArgumentException("Domain key must not be null or empty");
		}
		Objects.requireNonNull(fields);
		if (fields.length == 0) {
			throw new IllegalArgumentException("Fields must not be empty");
		}
		this.domainKey = domainKey;
		this.fields = fields;
	}

	@Override
	public boolean exists(Object... values) {
		if (values == null || fields == null) {
			throw new IllegalArgumentException("Values and fields must not be null");
		}
		if (values.length != fields.length) {
			throw new IllegalArgumentException("Values length must match fields length: " + Arrays.toString(fields));
		}
		return exists(domainKey, fields, values);
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
	 * @param fields    the fields corresponding to the values
	 * @param values    the value to check for uniqueness
	 * @return true if the value exists in the database, false otherwise
	 */
	protected abstract boolean exists(String domainKey, String[] fields, Object[] values);

	@Override
	public String getGroupId() {
		return domainKey;
	}
}
