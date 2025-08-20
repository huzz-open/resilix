package top.huzz.jaksho.api.validation;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import top.huzz.jaksho.api.cache.DomainMapperCache;
import top.huzz.jaksho.common.able.DomainDescription;
import top.huzz.jaksho.common.mapper.ExtBaseMapper;
import top.huzz.resilix.validation.ValidationContext;
import top.huzz.resilix.validation.Validations;
import top.huzz.resilix.validation.checker.DatasourceBasedChecker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param <T> the type of the entity being checked
 * @param <W> the type of the wrapper used for the check
 * @param <R> the type of the result returned by the check function
 * @author chenji
 * @since 1.0.0
 */
public abstract class AbstractDatasourceBasedCheckFunction<T extends DomainDescription, W, R> implements DatasourceBasedChecker.DatasourceBasedCheckFunction {

	private static final Map<String, ExtBaseMapper<?>> MAPPER_MAP = new HashMap<>();


	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void check(String domainKey, String[] fields, Object[] values) throws ConstraintViolationException {
		W wrapper = wrapperSupplier().get();
		BiConsumer<String, Object> fieldSetter = fieldSetter(wrapper);

		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			Object value = values[i];
			fieldSetter.accept(field, value);
		}

		ExtBaseMapper extBaseMapper = getMapper(domainKey);
		R checkResult = (R) wrapperFunction(extBaseMapper).apply(wrapper);

		// the filed is the db check filed, try transform to the entity check filed
		ValidationContext validationContext;
		String[] fieldsForBuildException = fields;
		if (useEntityFieldForExceptionBuild()
				&& (validationContext = Validations.getValidationContext()) != null) {
			fieldsForBuildException = validationContext.getEntityFields(fields);
		}
		ConstraintViolationException exception = buildException(checkResult, domainKey, fieldsForBuildException, values);
		if (exception != null) {
			throw exception;
		}
	}

	/**
	 * Builds a ConstraintViolationException based on the check result.
	 * This method should be implemented to provide the specific exception details.
	 *
	 * @param checkResult the result of the check
	 * @param domainKey   the key of the domain being checked
	 * @param fields      the fields that were checked
	 * @param values      the values that were checked
	 * @return a ConstraintViolationException if the check fails, null otherwise
	 */
	@Nullable
	protected abstract ConstraintViolationException buildException(R checkResult, String domainKey, String[] fields, Object[] values);

	/**
	 * Provides a function that applies the check to the wrapper.
	 * This method should be implemented to provide the specific check logic.
	 *
	 * @param extBaseMapper the mapper used to perform the check
	 * @return a function that takes a wrapper and returns the result of the check
	 */
	protected abstract Function<W, R> wrapperFunction(ExtBaseMapper<T> extBaseMapper);

	/**
	 * Provides a field setter for the wrapper used in the check function.
	 * This method can be overridden to provide a different way of setting fields in the wrapper.
	 *
	 * @param wrapper the wrapper to set fields on
	 * @return a BiConsumer that sets fields in the wrapper
	 */
	@Nonnull
	protected abstract BiConsumer<String, Object> fieldSetter(W wrapper);

	/**
	 * Provides a supplier for the wrapper used in the check function.
	 * This method can be overridden to provide a different type of wrapper if needed.
	 *
	 * @return a supplier that creates a new instance of the wrapper
	 */
	@Nonnull
	protected abstract Supplier<W> wrapperSupplier();

	/**
	 * Indicates whether the entity field should be used for building exceptions.
	 *
	 * @return true if the entity field should be used, false otherwise
	 */
	protected boolean useEntityFieldForExceptionBuild() {
		return true;
	}

	static ExtBaseMapper<?> getMapper(String domainKey) {
		ExtBaseMapper<?> baseMapper = MAPPER_MAP.get(domainKey);
		if (baseMapper == null) {
			synchronized (MAPPER_MAP) {
				baseMapper = MAPPER_MAP.get(domainKey);
				if (baseMapper == null) {
					baseMapper = createMapper(domainKey);
					MAPPER_MAP.put(domainKey, baseMapper);
				}
			}
		}
		return baseMapper;
	}

	private static ExtBaseMapper<?> createMapper(String domainKey) {
		return DomainMapperCache.getMapper(domainKey);
	}
}
