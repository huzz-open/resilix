package top.huzz.resilix.validation.checker;

import jakarta.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenji
 * @since 1.0.0
 */
public abstract class AbstractDatasourceBasedCheckerProvider implements DatasourceBasedCheckerProvider {
    protected final Map<String, Checker> checkerMap = new HashMap<>();
    private static final ThreadLocal<String> DOMAIN_KEY_TL = new ThreadLocal<>();
    private static final ThreadLocal<String[]> FIELDS_TL = new ThreadLocal<>();

    @Override
    public Checker getChecker(String domainKey, String... fields) {
        DOMAIN_KEY_TL.set(domainKey);
        FIELDS_TL.set(fields);
        try {
            return doGetChecker(domainKey, fields);
        } finally {
            DOMAIN_KEY_TL.remove();
            FIELDS_TL.remove();
        }
    }

    private Checker doGetChecker(String domainKey, String... fields) {
        String groupId = getGroupId();
        Checker checker = checkerMap.get(groupId);
        if (checker == null) {
            synchronized (checkerMap) {
                checker = checkerMap.get(groupId);
                if (checker == null) {
                    checker = createChecker(domainKey, fields);
                    checkerMap.put(groupId, checker);
                }
            }
        }
        return checker;
    }

    protected abstract Checker createChecker(String domainKey, String... fields);

    @Override
    public String getDomainKey() {
        return DOMAIN_KEY_TL.get();
    }

    @Nonnull
    @Override
    public String[] getFields() {
        return FIELDS_TL.get();
    }
}
