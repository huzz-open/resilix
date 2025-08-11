package top.huzz.resilix.evaluation;

import jakarta.annotation.Nonnull;

/**
 * 表达式变量分析器
 *
 * @author huzz
 * @since 1.0.2
 */
public interface ExpressionVariableAnalyser<T> {
    /**
     * @return 变量分析结果
     */
    @Nonnull
    ExpressionVariableAnalyserResult analyse(String expression);
}
