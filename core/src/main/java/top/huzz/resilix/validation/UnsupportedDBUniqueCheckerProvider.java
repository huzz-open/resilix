package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;

/**
 * @author huzz
 * @since 1.0.2
 */
public class UnsupportedDBUniqueCheckerProvider implements DBUniqueCheckerProvider {
	@Nonnull
	@Override
	public DBUniqueChecker getChecker(String domainKey, String... fields) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
