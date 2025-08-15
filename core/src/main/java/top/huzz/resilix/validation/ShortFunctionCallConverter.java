package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;
import top.huzz.resilix.validation.annotation.BizCheck;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Converts shorthand function-only expressions into valid SpEL function invocations.
 * <p>
 * When {@link BizCheck} is applied at field level, developers may write an expression that is
 * only a function name (with or without a leading '#'), e.g. {@code #__VALID} or {@code __VALID}.
 * Such expressions are not valid SpEL invocations on their own. This converter normalizes them to
 * {@code #__VALID(#this)} by implicitly using the current field value as the argument.
 * </p>
 *
 * <h3>Rules</h3>
 * <ul>
 *   <li>If the trimmed expression equals a registered function name (as returned by
 *   {@link Validations#getFunctions()}), optionally prefixed with '#', it will be converted to
 *   {@code #{functionName}(#this)}.</li>
 *   <li>If not matched, the original expression is returned unchanged.</li>
 * </ul>
 *
 * <p>This class is stateless and thread-safe.</p>
 *
 * @author chenji
 * @since 1.0.2
 */
public final class ShortFunctionCallConverter implements ExpressionConverter {

    @Override
    @Nonnull
    public String convert(String expression, BizCheck bizCheck) {
        if (expression == null) {
            return "";
        }
        String trimmed = expression.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }

        // Remove optional leading '#'
        String maybeFunctionName = trimmed.charAt(0) == '#' ? trimmed.substring(1) : trimmed;

        Map<String, Method> functions = Validations.getFunctions();
        if (functions.containsKey(maybeFunctionName)) {
            return "#" + maybeFunctionName + "(#this)";
        }
        return trimmed;
    }
}


