package top.huzz.resilix.util;

import jakarta.annotation.Nonnull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import top.huzz.resilix.exception.NewInstanceException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author huzz
 * @since 1.0.2
 */
public final class ReflectionUtils {
	public static final String[] DEFAULT_SCAN_PACKAGES = {
			"top.huzz",
	};

	/**
	 * create a new instance of the given class using its no-arg constructor
	 *
	 * @param clazz the class to instantiate
	 * @param <T>   the type of the class
	 * @return the new instance
	 * @throws NewInstanceException if the class cannot be instantiated, which may occur if the class does not have a no-argument constructor or if instantiation fails for other reasons.
	 */
	public static <T> T newInstance(Class<T> clazz) throws NewInstanceException {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new NewInstanceException(clazz, e);
		}
	}

	/**
	 * create a new instance of the class with the given name using its no-arg constructor
	 *
	 * @param className the fully qualified name of the class to instantiate
	 * @param <T>       the type of the class
	 * @return the new instance
	 * @throws NewInstanceException if the class cannot be instantiated, which may occur if the class does not exist, does not have a no-argument constructor, or instantiation fails for other reasons.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className) throws NewInstanceException {
		Class<?> clazz = forName(className);
		return (T) newInstance(clazz);
	}

	/**
	 * Get the Class object for the given class name.
	 *
	 * @param className the fully qualified name of the class
	 * @param <T>       the type of the class
	 * @return the Class object for the specified class
	 * @throws NewInstanceException if the class cannot be found
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> forName(String className) throws NewInstanceException {
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new NewInstanceException(className, e);
		}
	}

	/**
	 * Scan for methods annotated with the specified annotation type in the given base packages.
	 *
	 * @param annotationType the annotation type to look for
	 * @param basePackages   the base packages to scan
	 * @return a set of methods annotated with the specified annotation type
	 */
	@Nonnull
	public static Set<Method> reflectionsScanMethods(Class<? extends Annotation> annotationType,
													 String... basePackages) {
		Set<Method> result = new LinkedHashSet<>();
		for (String pkg : basePackages) {
			Reflections reflections = new Reflections(pkg, Scanners.MethodsAnnotated);
			result.addAll(reflections.getMethodsAnnotatedWith(annotationType));
		}
		return result;
	}

	/**
	 * Get a method from the specified class by its name and parameter types.
	 *
	 * @param clazz          the class to search in
	 * @param methodName     the name of the method
	 * @param parameterTypes the parameter types of the method, can be null or empty for no-arg methods
	 * @return the Method object if found
	 * @throws IllegalArgumentException if no method is found or if multiple methods match the criteria
	 */
	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		int length = parameterTypes == null ? 0 : parameterTypes.length;
		Method[] uniqueDeclaredMethods = org.springframework.util.ReflectionUtils.getUniqueDeclaredMethods(clazz, method -> {
			if (!method.getName().equals(methodName)) {
				return false;
			}
			if (parameterTypes == null || parameterTypes.length == 0) {
				return method.getParameterCount() == 0;
			}
			if (method.getParameterCount() == length) {
				Class<?>[] methodParameterTypes = method.getParameterTypes();
				for (int i = 0; i < methodParameterTypes.length; i++) {
					if (!methodParameterTypes[i].equals(parameterTypes[i])) {
						return false;
					}
				}
				return true;
			}
			return false;
		});
		if (uniqueDeclaredMethods.length == 0) {
			throw new IllegalArgumentException("No method found with name: " + methodName + " and parameter types: " + java.util.Arrays.toString(parameterTypes));
		} else if (uniqueDeclaredMethods.length > 1) {
			throw new IllegalArgumentException("Multiple methods found with name: " + methodName + " and parameter types: " + java.util.Arrays.toString(parameterTypes));
		}
		Method method = uniqueDeclaredMethods[0];
		org.springframework.util.ReflectionUtils.makeAccessible(method);
		return method;
	}
}
