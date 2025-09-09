package top.huzz.jaksho.api.validation;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import top.huzz.jaksho.common.session.Session;
import top.huzz.resilix.validation.LeadingFieldValue;
import top.huzz.resilix.validation.LeadingFieldValueProvider;
import top.huzz.resilix.validation.checker.Checker;
import top.huzz.resilix.validation.checker.TypedDatasourceBasedChecker;

import java.util.List;

/**
 * @author chenji
 * @since 1.0.0
 */
@Component
public class SessionWorkspaceIdLeadingFieldValueProvider implements LeadingFieldValueProvider {
    @Nonnull
    @Override
    public LeadingFieldValue get() {
        // Here you should implement the logic to get the actual workspaceId from the session or context
        return new LeadingFieldValue("workspaceId", Session.currentWorkspaceId());
    }

    @Override
    public boolean shouldAsLeading(PredicateInput input) {
        Checker.Type type = input.type();
        List<String> cfs = input.cfs();
        // If the only checked field is "id" and the check type is UNIQUE or EXIST, we assume it's a primary key check and do not add leading fields
        boolean usePrimaryKey = cfs.size() == 1 && StringUtils.equalsIgnoreCase(cfs.get(0), "id")
                && (type == TypedDatasourceBasedChecker.BuiltIn.UNIQUE || type == TypedDatasourceBasedChecker.BuiltIn.EXIST);
        return !usePrimaryKey;
    }
}
