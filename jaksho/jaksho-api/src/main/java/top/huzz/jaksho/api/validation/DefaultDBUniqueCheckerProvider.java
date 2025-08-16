package top.huzz.jaksho.api.validation;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import top.huzz.resilix.validation.DBUniqueChecker;
import top.huzz.resilix.validation.DBUniqueCheckerProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huzz
 * @since 1.0.2
 */
@Component
public class DefaultDBUniqueCheckerProvider implements DBUniqueCheckerProvider {
	private static final Map<String, DBUniqueChecker> UNIQUE_CHECKER_MAP = new HashMap<>();

	public static DBUniqueChecker getChecker(String domainKey, String field) {
		DBUniqueChecker dbUniqueChecker = UNIQUE_CHECKER_MAP.get(domainKey);
		if (dbUniqueChecker == null) {
			synchronized (UNIQUE_CHECKER_MAP) {
				dbUniqueChecker = UNIQUE_CHECKER_MAP.get(domainKey);
				if (dbUniqueChecker == null) {
					dbUniqueChecker = createChecker(domainKey, field);
					UNIQUE_CHECKER_MAP.put(domainKey, dbUniqueChecker);
				}
			}
		}
		return dbUniqueChecker;
	}

	private static DBUniqueChecker createChecker(String domainKey, String field) {
		return new DefaultOneFieldDBUniqueChecker(domainKey, field);
	}

	@Nonnull
	@Override
	public DBUniqueChecker getChecker(String domainKey, String... fields) {
		return getChecker(domainKey, fields[0]);
	}
}
