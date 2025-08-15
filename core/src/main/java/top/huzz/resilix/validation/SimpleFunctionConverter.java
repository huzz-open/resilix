package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;
import top.huzz.resilix.validation.annotation.BizCheck;

/**
 * @author chenji
 * @since 1.0.0
 */
public class SimpleFunctionConverter implements ExpressionConverter {
    @Override
    @Nonnull
    public String convert(String expression, BizCheck bizCheck) {
        // 对于那些表达式等于功能名称的情况（实际上是不合法的表达式），做一下简单转换
        // 例如：@BizCheck("#__VALID") -> @BizCheck("__VALID(#this)")
        // 这种情况只适用于@BizCheck加在字段上时

        return "";
    }
}
