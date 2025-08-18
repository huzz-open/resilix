package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import top.huzz.resilix.validation.annotation.BizCheckFunction;
import top.huzz.resilix.validation.checker.Checker;
import top.huzz.resilix.validation.checker.TypedDatasourceBasedChecker;
import top.huzz.resilix.validation.checker.TypedDatasourceBasedCheckerProvider;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author chenji
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("unused")
public class Validations implements ApplicationRunner {

	private static final ThreadLocal<ValidationContext> VALIDATION_CONTEXT_TL = new ThreadLocal<>();
	private static List<ReducibleMethodProvider> reducibleMethodProviders;
	private static Validator validator;
	private static TypedDatasourceBasedCheckerProvider typedDatasourceBasedCheckerProvider;

	public Validations(List<ReducibleMethodProvider> reducibleMethodProviders, Validator validator, TypedDatasourceBasedCheckerProvider typedDatasourceBasedCheckerProvider) {
		Validations.reducibleMethodProviders = reducibleMethodProviders;
		Validations.validator = validator;
		Validations.typedDatasourceBasedCheckerProvider = typedDatasourceBasedCheckerProvider;
	}

	private static final Map<String, Method> METHODS = new LinkedHashMap<>();

	/**
	 * Gets the current validation context.
	 *
	 * @return the current {@link ValidationContext} instance
	 */
	public static ValidationContext getValidationContext() {
		return VALIDATION_CONTEXT_TL.get();
	}

	/**
	 * Sets the current validation context.
	 *
	 * @param context the {@link ValidationContext} instance to set
	 */
	public static void setValidationContext(ValidationContext context) {
		VALIDATION_CONTEXT_TL.set(context);
	}

	/**
	 * Clears the current validation context.
	 */
	public static void clearValidationContext() {
		VALIDATION_CONTEXT_TL.remove();
	}

	/**
	 * Registers a new business check function.
	 * <p>
	 * This method allows you to register custom validation functions that can be used in {@link top.huzz.resilix.validation.annotation.BizCheck} annotations.
	 * </p>
	 *
	 * @param functionName the name of the function to register
	 * @param method       the method that implements the business check logic
	 * @throws IllegalArgumentException if the function name is already registered
	 */
	public static void registerFunctions(@Nonnull String functionName, @Nonnull Method method) {
		Objects.requireNonNull(functionName);
		if (METHODS.containsKey(functionName)) {
			throw new IllegalArgumentException("Function '" + functionName + "' is already registered.");
		}
		METHODS.put(functionName, method);
	}

	/**
	 * Returns an unmodifiable map of all registered business check functions.
	 *
	 * @return an unmodifiable map where keys are function names and values are the corresponding methods
	 */
	@Nonnull
	public static Map<String, Method> getFunctions() {
		return Collections.unmodifiableMap(METHODS);
	}

	/**
	 * Finds all reducible methods provided by registered {@link ReducibleMethodProvider}s.
	 * <p>
	 * This method scans all registered {@link ReducibleMethodProvider} instances and collects their methods into a map.
	 * The map keys are the function names, and the values are the corresponding methods.
	 * </p>
	 *
	 * @return a map of function names to methods
	 */
	@Nonnull
	private static Map<String, Method> findAllReducibleMethod() {
		Map<String, Method> functions = new LinkedHashMap<>();
		reducibleMethodProviders.stream().map(ReducibleMethodProvider::methods).forEach(functions::putAll);
		return functions;
	}

	/**
	 * Validations for data sources.
	 */
	private static class DatasourceValidations {

		/**
		 * Checks if the provided object is unique in the specified domain with specific fields.
		 * <p>
		 * This method uses the registered {@link ValidationContext} to perform a custom validation check for uniqueness.
		 * </p>
		 *
		 * @param domainKey a key representing the domain in which to check uniqueness
		 * @param fields    optional fields to specify which attributes to check for uniqueness, split by commas
		 * @param any       the object or objects to check for uniqueness
		 * @return true if the object is unique, false otherwise
		 * @throws ConstraintViolationException if the domain key is null or blank, or if the object to check is null
		 */
		@BizCheckFunction("__DB_UNIQUE_WITH")
		private static boolean uniqueWith(String domainKey, @Nullable String fields, Object... any) {
			return valid(TypedDatasourceBasedChecker.BuiltIn.UNIQUE, domainKey, fields, any);
		}

		/**
		 * Checks if the provided object is unique in the specified domain with a specific type and the field "id".
		 * <p>
		 * This method uses the registered {@link ValidationContext} to perform a custom validation check for uniqueness.
		 * </p>
		 *
		 * @param domainKey a key representing the domain in which to check uniqueness
		 * @param any       the object or objects to check for uniqueness
		 * @return true if the object is unique, false otherwise
		 * @throws ConstraintViolationException if the domain key is null or blank, or if the object to check is null
		 */
		@BizCheckFunction("__DB_UNIQUE_WITH_ID")
		private static boolean uniqueWithId(String domainKey, Object... any) {
			return uniqueWith(domainKey, "id", any);
		}

		/**
		 * Checks if the provided object is unique in the specified domain. the fields to check is the annotated field.
		 * <p>
		 * This method uses the registered {@link ValidationContext} to perform a custom validation check for uniqueness.
		 * </p>
		 *
		 * @param domainKey a key representing the domain in which to check uniqueness
		 * @param any       the object or objects to check for uniqueness
		 * @return true if the object is unique, false otherwise
		 * @throws ConstraintViolationException if the domain key is null or blank, or if the object to check is null
		 */
		@BizCheckFunction("__DB_UNIQUE")
		private static boolean unique(String domainKey, Object... any) {
			return uniqueWith(domainKey, null, any);
		}


		/**
		 * Checks if the provided object exists in the specified domain with specific fields.
		 * <p>
		 * This method uses the registered {@link ValidationContext} to perform a custom validation check for existence.
		 * </p>
		 *
		 * @param domainKey a key representing the domain in which to check existence
		 * @param fields    optional fields to specify which attributes to check for existence, split by commas
		 * @param any       the object or objects to check for existence
		 * @return true if the object exists, false otherwise
		 * @throws ConstraintViolationException if the domain key is null or blank, or if the object to check is null
		 */
		@BizCheckFunction("__DB_EXIST_WITH")
		private static boolean existWith(String domainKey, @Nullable String fields, Object... any) {
			return valid(TypedDatasourceBasedChecker.BuiltIn.EXIST, domainKey, fields, any);
		}

		/**
		 * Checks if the provided object exists in the specified domain with a specific type and the field "id".
		 * <p>
		 * This method uses the registered {@link ValidationContext} to perform a custom validation check for existence.
		 * </p>
		 *
		 * @param domainKey a key representing the domain in which to check existence
		 * @param any       the object or objects to check for existence
		 * @return true if the object exists, false otherwise
		 * @throws ConstraintViolationException if the domain key is null or blank, or if the object to check is null
		 */
		@BizCheckFunction("__DB_EXIST_WITH_ID")
		private static boolean existWithId(String domainKey, Object... any) {
			return existWith(domainKey, "id", any);
		}

		/**
		 * Checks if the provided object exists in the specified domain. the fields to check is the annotated field.
		 * <p>
		 * This method uses the registered {@link ValidationContext} to perform a custom validation check for existence.
		 * </p>
		 *
		 * @param domainKey a key representing the domain in which to check existence
		 * @param any       the object or objects to check for existence
		 * @return true if the object exists, false otherwise
		 * @throws ConstraintViolationException if the domain key is null or blank, or if the object to check is null
		 */
		@BizCheckFunction("__DB_EXIST")
		private static boolean exist(String domainKey, Object... any) {
			return existWith(domainKey, null, any);
		}

		/**
		 * Checks if the provided object is unique in the specified domain with a specific type.
		 * <p>
		 * This method uses the registered {@link ValidationContext} to perform a custom validation check for uniqueness.
		 * </p>
		 *
		 * @param type      the type of the checker
		 * @param domainKey a key representing the domain in which to check uniqueness
		 * @param fields    optional fields to specify which attributes to check for uniqueness, split by commas
		 * @param any       the object or objects to check for uniqueness
		 * @return true if the object is unique, false otherwise
		 * @throws ConstraintViolationException if the domain key is null or blank, or if the object to check is null
		 */
		public static boolean valid(TypedDatasourceBasedChecker.Type type, String domainKey, @Nullable String fields, Object... any) {
			if (domainKey == null || domainKey.isBlank()) {
				throw new ConstraintViolationException("Domain key cannot be null or blank", Collections.emptySet());
			}
			if (any == null) {
				throw new ConstraintViolationException("Object to check uniqueness cannot be null", Collections.emptySet());
			}

			ValidationContext validationContext = getValidationContext();
			Objects.requireNonNull(validationContext, "Validation context must not be null");

			validationContext.executeCustomValidation(
					(constraintDescriptor, path) -> {
						String[] fieldArr = new String[]{path.asString()};
						if (fields != null) {
							fieldArr = fields.split(",");
						}
						Checker checker = typedDatasourceBasedCheckerProvider.getChecker(type, domainKey, fieldArr);
						checker.check(any);
					});
			return true;
		}
	}


	/**
	 * Validates the provided object or collection of objects.
	 * <p>
	 * This method uses the registered {@link Validator} to validate the input object(s).
	 * It supports various types of inputs, including single objects, arrays, iterables, optionals, and maps.
	 * </p>
	 *
	 * @param any the object or collection to validate
	 * @throws ConstraintViolationException if validation fails
	 */
	@BizCheckFunction("__VALID")

	private static void valid(Object any) {
		if (any == null) {
			throw new ConstraintViolationException("Object to validate cannot be null", java.util.Collections.emptySet());
		}
		Validator v = validator;

		Set<ConstraintViolation<Object>> result = new LinkedHashSet<>();

		Consumer<Object> validateOne = o -> {
			if (o == null) return;
			Set<ConstraintViolation<Object>> vs = v.validate(o);
			result.addAll(vs);
		};

		if (any instanceof Iterable<?> it) {
			int i = 0;
			for (Object e : it) {
				Set<ConstraintViolation<Object>> vs = v.validate(e);
				result.addAll(vs);
				i++;
			}
			if (i == 0) {
				throw new ConstraintViolationException("Iterable to validate cannot be empty", Collections.emptySet());
			}
		} else if (any.getClass().isArray()) {
			int n = java.lang.reflect.Array.getLength(any);
			if (n == 0) {
				throw new ConstraintViolationException("Array to validate cannot be empty", Collections.emptySet());
			}
			for (int i = 0; i < n; i++) {
				Object e = java.lang.reflect.Array.get(any, i);
				Set<ConstraintViolation<Object>> vs = v.validate(e);
				result.addAll(vs);
			}
		} else if (any instanceof java.util.Optional<?> opt) {
			opt.ifPresent(validateOne);
		} else if (any instanceof java.util.Map<?, ?> m) {
			if (m.isEmpty()) {
				throw new ConstraintViolationException("Map to validate cannot be empty", Collections.emptySet());
			}
			for (var en : m.entrySet()) {
				Set<ConstraintViolation<Object>> vs = v.validate(en.getValue());
				result.addAll(vs);
			}
		} else {
			validateOne.accept(any);
		}

		if (!result.isEmpty()) {
			throw new ConstraintViolationException(result);
		}
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Map<String, Method> allReducibleMethod = findAllReducibleMethod();
		log.info("All reducible methods: {}", allReducibleMethod.keySet());
		allReducibleMethod.forEach(Validations::registerFunctions);
	}
}
