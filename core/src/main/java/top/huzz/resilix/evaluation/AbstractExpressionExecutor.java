package top.huzz.resilix.evaluation;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huzz
 * @since 1.0.2
 */
@Slf4j
public abstract class AbstractExpressionExecutor implements ExpressionExecutor {
    final ExpressionParser expressionParser;
    final List<ExpressionVariableAnalyser<?>> analysers;
    final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    protected AbstractExpressionExecutor(ExpressionParser expressionParser, List<ExpressionVariableAnalyser<?>> analysers) {
        this.expressionParser = expressionParser;
        this.analysers = analysers;
    }

    @Nonnull
    @Override
    public ExpressionExecuteResult execute(@Nonnull ExtEvaluationContext context, @Nonnull String expression) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(expression);
        context.fill(expression, analysers);

        Expression spELExpressionObj = expressionCache.computeIfAbsent(expression, expressionParser::parseExpression);
        Object origin = spELExpressionObj.getValue(context);
        return new ExpressionExecuteResult(origin);
    }
}
