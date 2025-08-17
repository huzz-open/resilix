package top.huzz.resilix.validation.checker;

/**
 * @author chenji
 * @since 1.0.0
 */
public class TypedDatasourceBasedCheckerProvider extends AbstractDatasourceBasedCheckerProvider {
    private static final ThreadLocal<TypedDatasourceBasedChecker.Type> TYPE_TL = new ThreadLocal<>();

    protected final DatasourceBasedCheckFunctionProvider datasourceBasedCheckFunctionProvider;

    public TypedDatasourceBasedCheckerProvider(DatasourceBasedCheckFunctionProvider datasourceBasedCheckFunctionProvider) {
        this.datasourceBasedCheckFunctionProvider = datasourceBasedCheckFunctionProvider;
    }

    @Override
    protected Checker createChecker(String domainKey, String... fields) {
        try {
            return createChecker(TYPE_TL.get(), domainKey, fields);
        } finally {
            TYPE_TL.remove();
        }
    }

    /**
     * Creates a Checker instance based on the provided type, domain key, fields, and values.
     *
     * @param type      the type of the checker
     * @param domainKey the domain key for the checker
     * @param fields    the fields to be checked
     * @return a Checker instance that can validate the provided values
     */
    protected Checker createChecker(TypedDatasourceBasedChecker.Type type, String domainKey, String... fields) {
        return new TypedDatasourceBasedChecker(datasourceBasedCheckFunctionProvider, type, domainKey, fields);
    }

    /**
     * Returns a Checker instance based on the provided type, domain key, fields, and values.
     *
     * @param type      the type of the checker
     * @param domainKey the domain key for the checker
     * @param fields    the fields to be checked
     * @return a Checker instance that can validate the provided values
     */
    public Checker getChecker(TypedDatasourceBasedChecker.Type type, String domainKey, String... fields) {
        TYPE_TL.set(type);
        return getChecker(domainKey, fields);
    }
}
