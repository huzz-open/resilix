package top.huzz.resilix.evaluation;

import jakarta.annotation.Nonnull;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

/**
 * @author huzz
 * @since 1.0.2
 */
public class ExtStandardEvaluationContext extends StandardEvaluationContext implements ExtEvaluationContext {



    @Override
    public void fill(@Nonnull String expression, List<ExpressionVariableAnalyser<?>> analysers) {

    }
}
