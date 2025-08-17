package top.huzz.jaksho.api.validation;

import org.springframework.stereotype.Component;
import top.huzz.resilix.validation.checker.DatasourceBasedCheckFunctionProvider;
import top.huzz.resilix.validation.checker.DatasourceBasedChecker;
import top.huzz.resilix.validation.checker.TypedDatasourceBasedChecker;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chenji
 * @since 1.0.0
 */
@Component
public final class DefaultDatasourceBasedCheckFunctionProvider implements DatasourceBasedCheckFunctionProvider {

    private static Map<TypedDatasourceBasedChecker.Type, DatasourceBasedChecker.DatasourceBasedCheckFunction> checkFunctionMap;

    public DefaultDatasourceBasedCheckFunctionProvider(List<TypedDatasourceBasedCheckFunction<?, ?, ?>> checkFunctions) {
        DefaultDatasourceBasedCheckFunctionProvider.checkFunctionMap = checkFunctions.stream()
                .collect(Collectors.toMap(TypedDatasourceBasedCheckFunction::getType,
                        Function.identity()));
    }

    @Override
    public DatasourceBasedChecker.DatasourceBasedCheckFunction getCheckFunction(TypedDatasourceBasedChecker.Type type, String domainKey, String... fields) {
        return checkFunctionMap.get(type);
    }
}
