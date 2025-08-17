package top.huzz.resilix.validation.checker;

/**
 * @author chenji
 * @since 1.0.0
 */
public interface CheckerProvider {
    /**
     * Returns a Checker instance based on the provided values.
     *
     * @param values the values to be checked
     * @return a Checker instance that can validate the provided values
     */
    Checker getChecker(Object[] values);
}
