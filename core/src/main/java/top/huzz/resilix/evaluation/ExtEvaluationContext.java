package top.huzz.resilix.evaluation;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.expression.EvaluationContext;

import java.util.List;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface ExtEvaluationContext extends EvaluationContext {
    /**
     * 往上下文中添加对象
     *
     * @param expression 表达式
     * @param analysers  变量分析器
     */
    void fill(@Nonnull String expression, @NotEmpty List<ExpressionVariableAnalyser<?>> analysers);

}
