package top.huzz.resilix.util;

import top.huzz.resilix.exception.NewInstanceException;

/**
 * @author huzz
 * @since 1.0.2
 */
public final class ReflectionUtils {
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
}
