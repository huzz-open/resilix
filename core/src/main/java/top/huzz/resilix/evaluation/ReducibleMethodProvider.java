package top.huzz.resilix.evaluation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface ReducibleMethodProvider {
    Collection<Class<?>> classes();

    Map<String, Method> methods();
}
