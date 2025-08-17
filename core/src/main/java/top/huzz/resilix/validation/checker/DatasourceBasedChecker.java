package top.huzz.resilix.validation.checker;

import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolationException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Based interface for checkers that validate values against a datasource.
 *
 * @author huzz
 * @since 1.0.2
 */
public interface DatasourceBasedChecker extends Checker, GroupedDatasourceBasedOperationDescription {
    /**
     * Gets the check function that performs the validation against the datasource.
     *
     * @return a DatasourceBasedCheckFunction instance that checks the values against the datasource
     */
    @Nonnull
    DatasourceBasedCheckFunction getCheckFunction();

    @Override
    default void check(Object... values) throws ConstraintViolationException {
        String[] fields = getFields();
        Objects.requireNonNull(fields);
        String domainKey = getDomainKey();
        if (values.length != fields.length) {
            throw new IllegalArgumentException("Values length must match fields length: " + Arrays.toString(fields));
        }
        getCheckFunction().check(domainKey, fields, values);
    }


    interface DatasourceBasedCheckFunction {
        /**
         * Checks the validity of the provided values against the datasource.
         *
         * @param domainKey the key of the domain
         * @param fields    the fields to check
         * @param values    the values to check
         * @throws ConstraintViolationException if the values violate any constraints
         */
        void check(String domainKey, String[] fields, Object[] values) throws ConstraintViolationException;
    }
}
