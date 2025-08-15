package top.huzz.resilix.validation;

import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import top.huzz.resilix.validation.annotation.BizCheckFunction;

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

    private static List<ReducibleMethodProvider> reducibleMethodProviders;
    private static Validator validator;

    public Validations(List<ReducibleMethodProvider> reducibleMethodProviders, Validator validator) {
        Validations.reducibleMethodProviders = reducibleMethodProviders;
        Validations.validator = validator;
    }

    private static final Map<String, Method> METHODS = new LinkedHashMap<>();

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

    @BizCheckFunction("__UNIQUE")
    private static void unique(Object any) {
        System.out.println(any);
    }

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
