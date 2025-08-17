package top.huzz.resilix.validation.checker;

/**
 * @author chenji
 * @since 1.0.0
 */
public interface DatasourceBasedCheckerProvider extends CheckerProvider, GroupedDatasourceBasedOperationDescription {
    @Override
    default Checker getChecker(Object[] values) {
        return getChecker(getDomainKey(), getFields());
    }

    /**
     * Returns a Checker instance based on the provided domain key, fields, and values.
     *
     * @param domainKey the domain key for the checker
     * @param fields    the fields to be checked
     * @return a Checker instance that can validate the provided values
     */
    Checker getChecker(String domainKey, String... fields);
}
