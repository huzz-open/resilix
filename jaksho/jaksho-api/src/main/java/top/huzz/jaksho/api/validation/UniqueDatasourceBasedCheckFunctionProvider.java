package top.huzz.jaksho.api.validation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.common.mapper.ExtBaseMapper;
import top.huzz.resilix.validation.checker.TypedDatasourceBasedChecker;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author huzz
 * @since 1.0.2
 */
@Component
public class UniqueDatasourceBasedCheckFunctionProvider<T extends BasicProperties> extends TypedDatasourceBasedCheckFunction<T, QueryWrapper<T>, Boolean> {
    @Nullable
    @Override
    protected ConstraintViolationException buildException(Boolean checkResult, String domainKey, String[] fields, Object[] values) {
        if (checkResult) {
            StringBuilder errorMessage = new StringBuilder("Unique constraint violation for fields: ");
            for (String field : fields) {
                errorMessage.append(field).append(", ");
            }
            errorMessage.setLength(errorMessage.length() - 2); // Remove trailing comma and space
            return new ConstraintViolationException(errorMessage.toString(), null);
        }
        return null;
    }

    @Override
    protected Function<QueryWrapper<T>, Boolean> wrapperFunction(ExtBaseMapper<T> extBaseMapper) {
        return extBaseMapper::exists;
    }

    @Nonnull
    @Override
    protected BiConsumer<String, Object> fieldSetter(QueryWrapper<T> wrapper) {
        return wrapper::eq;
    }

    @Nonnull
    @Override
    protected Supplier<QueryWrapper<T>> wrapperSupplier() {
        return Wrappers::query;
    }

    @Override
    public TypedDatasourceBasedChecker.Type getType() {
        return TypedDatasourceBasedChecker.BuiltIn.UNIQUE;
    }
}
