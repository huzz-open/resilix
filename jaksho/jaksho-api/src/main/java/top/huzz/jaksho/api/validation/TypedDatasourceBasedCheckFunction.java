package top.huzz.jaksho.api.validation;

import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.resilix.validation.checker.TypedDatasourceBasedChecker;

/**
 * @author chenji
 * @since 1.0.0
 */
public abstract class TypedDatasourceBasedCheckFunction<T extends BasicProperties, W, R> extends AbstractDatasourceBasedCheckFunction<T, W, R> {
    /**
     * @return the type of the checker function
     */
    public abstract TypedDatasourceBasedChecker.Type getType();
}
