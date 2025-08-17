package top.huzz.resilix.validation.checker;

/**
 * @author chenji
 * @since 1.0.0
 */
public class NotImplDatasourceBasedCheckFunctionProvider implements DatasourceBasedCheckFunctionProvider {
    @Override
    public DatasourceBasedChecker.DatasourceBasedCheckFunction getCheckFunction(TypedDatasourceBasedChecker.Type type, String domainKey, String... fields) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
