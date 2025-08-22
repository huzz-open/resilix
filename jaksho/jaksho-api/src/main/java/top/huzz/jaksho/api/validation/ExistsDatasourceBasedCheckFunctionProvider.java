package top.huzz.jaksho.api.validation;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.resilix.validation.checker.TypedDatasourceBasedChecker;

/**
 * @author huzz
 * @since 1.0.2
 */
@Component
public class ExistsDatasourceBasedCheckFunctionProvider<T extends BasicProperties> extends UniqueDatasourceBasedCheckFunctionProvider<T> {
    @Nullable
    @Override
    protected ConstraintViolationException buildException(Boolean checkResult, String domainKey, String[] fields, Object[] values) {
        if (!checkResult) {
            StringBuilder errorMessage = new StringBuilder("Entity must exist for fields: ");
            for (String field : fields) {
                errorMessage.append(field).append(", ");
            }
            errorMessage.setLength(errorMessage.length() - 2); // Remove trailing comma and space
            return new ConstraintViolationException(errorMessage.toString(), null);
        }
        return null;
    }

    @Override
    public TypedDatasourceBasedChecker.Type getType() {
        return TypedDatasourceBasedChecker.BuiltIn.EXIST;
    }
}
