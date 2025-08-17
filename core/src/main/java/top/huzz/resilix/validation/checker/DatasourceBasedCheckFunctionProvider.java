package top.huzz.resilix.validation.checker;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface DatasourceBasedCheckFunctionProvider {
	/**
	 * Get a check function for the specified type, domain key, and fields.
	 *
	 * @param type      the type of the check function
	 * @param domainKey the key of the domain
	 * @param fields    the fields to check
	 * @return a DatasourceBasedCheckFunction instance that performs the check based on the specified parameters
	 */
	DatasourceBasedChecker.DatasourceBasedCheckFunction getCheckFunction(TypedDatasourceBasedChecker.Type type, String domainKey, String... fields);
}
