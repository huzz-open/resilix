package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;
import top.huzz.resilix.validation.checker.Checker;

import java.util.List;

/**
 * Leading field value provider. some database check need leading field value, such as workspaceId, tenantId, etc.
 * these leading field values may not be the entity field, so we need a provider to provide these leading field values.
 *
 * @author chenji
 * @since 1.0.0
 */
public interface LeadingFieldValueProvider {
    /**
     * Get leading field value.
     *
     * @return leading field value
     */
    @Nonnull
    LeadingFieldValue get();

    /**
     * @return true if this provider should be used to provide leading field values that are prepended to the check lists
     */
    default boolean shouldAsLeading(PredicateInput input) {
        return true;
    }

    /**
     * @param cfs checked fields
     * @param efs entity fields
     * @param cvs check values
     */
    record PredicateInput(Checker.Type type, List<String> cfs, List<String> efs, List<Object> cvs) {
    }
}
