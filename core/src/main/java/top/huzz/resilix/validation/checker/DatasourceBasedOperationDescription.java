package top.huzz.resilix.validation.checker;

import jakarta.annotation.Nonnull;

/**
 * @author chenji
 * @since 1.0.0
 */
public interface DatasourceBasedOperationDescription {
    /**
     * Gets the key of the domain associated with this checker.
     *
     * @return the domain key
     */
    String getDomainKey();

    /**
     * Gets the fields to be checked.
     *
     * @return an array of field names to check
     */
    @Nonnull
    String[] getFields();
}
