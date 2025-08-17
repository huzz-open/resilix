package top.huzz.resilix.validation.checker;

import jakarta.annotation.Nonnull;

import java.util.Objects;

/**
 * @author huzz
 * @since 1.0.2
 */
public abstract class AbstractDatasourceBasedChecker implements DatasourceBasedChecker {
    protected final String domainKey;
    protected final String[] fields;

    protected AbstractDatasourceBasedChecker(String domainKey, @Nonnull String... fields) {
        if (domainKey == null || domainKey.isEmpty()) {
            throw new IllegalArgumentException("Domain key must not be null or empty");
        }
        Objects.requireNonNull(fields);
        if (fields.length == 0) {
            throw new IllegalArgumentException("Fields must not be empty");
        }
        this.domainKey = domainKey;
        this.fields = fields;
    }

    @Override
    public String getDomainKey() {
        return domainKey;
    }

    @Nonnull
    @Override
    public String[] getFields() {
        return fields;
    }
}
