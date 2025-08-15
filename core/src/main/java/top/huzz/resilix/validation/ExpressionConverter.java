package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;
import top.huzz.resilix.validation.annotation.BizCheck;

/**
 * @author chenji
 * @since 1.0.0
 */
public interface ExpressionConverter {
    /**
     * Converts a given expression into a format suitable for evaluation.
     *
     * @param expression the expression to convert, typically a SpEL (Spring Expression Language) expression.
     * @param bizCheck   the BizCheck annotation instance that provides context for the conversion.
     * @return the converted expression as a String, ready for evaluation.
     */
    @Nonnull
    String convert(String expression, BizCheck bizCheck);
}
