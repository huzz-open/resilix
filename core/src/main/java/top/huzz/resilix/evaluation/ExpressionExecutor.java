package top.huzz.resilix.evaluation;

import jakarta.annotation.Nonnull;

/**
 * expression executor
 *
 * @author huzz
 * @since 1.0.2
 */
public interface ExpressionExecutor {
    /**
     * 执行表达式
     *
     * @param context    执行上下文
     * @param expression 表达式字符串，如果为空直接返回
     * @return 执行结果
     */
    @Nonnull
    ExpressionExecuteResult execute(@Nonnull ExtEvaluationContext context, @Nonnull String expression);
}
