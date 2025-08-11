package top.huzz.resilix.evaluation;

import lombok.Getter;

import java.util.Map;

/**
 * @author huzz
 * @since 1.0.2
 */
@Getter
public final class ExpressionVariableAnalyserResult {
    private final Map<String, Object> variables;

    public ExpressionVariableAnalyserResult(Map<String, Object> variables) {
        this.variables = variables;
    }
}
