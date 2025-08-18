package top.huzz.resilix.validation.checker;

import top.huzz.resilix.core.StringGroupable;

/**
 * @author chenji
 * @since 1.0.0
 */
public interface GroupedDatasourceBasedOperationDescription extends DatasourceBasedOperationDescription, StringGroupable {
    /**
     * Gets the group ID for this checker, which is a combination of the domain key and the fields.
     *
     * @return group ID as a string
     */
    @Override
    default String getGroupId() {
        return getDomainKey() + "|" +String.join(":", getFields());
    }
}
