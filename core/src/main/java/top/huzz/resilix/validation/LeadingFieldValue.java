package top.huzz.resilix.validation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author chenji
 * @since 1.0.0
 */
@Getter
@Setter
public final class LeadingFieldValue {
    private final String checkField;
    private final String entityField;
    private final Object value;

    public LeadingFieldValue(String checkField, Object value) {
        this(checkField, checkField, value);
    }

    public LeadingFieldValue(String checkField, String entityField, Object value) {
        this.checkField = checkField;
        this.entityField = entityField;
        this.value = value;
    }
}
