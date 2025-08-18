package top.huzz.resilix.validation.checker;

import jakarta.annotation.Nonnull;
import top.huzz.resilix.core.StringGroupable;

import java.util.Objects;

/**
 * @author huzz
 * @since 1.0.2
 */
public class TypedDatasourceBasedChecker extends AbstractDatasourceBasedChecker {
	protected final DatasourceBasedCheckFunctionProvider datasourceBasedCheckFunctionProvider;
	protected final Type type;

	protected TypedDatasourceBasedChecker(DatasourceBasedCheckFunctionProvider datasourceBasedCheckFunctionProvider, Type type, String domainKey, @Nonnull String... fields) {
		super(domainKey, fields);
		Objects.requireNonNull(type);
		Objects.requireNonNull(datasourceBasedCheckFunctionProvider);
		this.datasourceBasedCheckFunctionProvider = datasourceBasedCheckFunctionProvider;
		this.type = type;
	}

	@Nonnull
	@Override
	public DatasourceBasedCheckFunction getCheckFunction() {
		return datasourceBasedCheckFunctionProvider.getCheckFunction(type, domainKey, fields);
	}

	/**
	 * Interface representing the type of the checker.
	 */
	public interface Type extends StringGroupable {
		/**
		 * Returns the unique identifier for this checker type.
		 *
		 * @return the unique identifier
		 */
		@Nonnull
		String id();

		@Override
		default String getGroupId() {
			return id();
		}
	}

	/**
	 * Enum representing built-in checker types.
	 */
	public enum BuiltIn implements Type {
		/**
		 * Check if unique
		 */
		UNIQUE("__unique"),

		/**
		 * Check if exists
		 */
		EXIST("__exist"),

		;

		private final String id;

		BuiltIn(String id) {
			this.id = id;
		}

		@Nonnull
		@Override
		public String id() {
			return id;
		}
	}
}
