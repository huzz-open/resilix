package top.huzz.resilix.evaluation;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface ReducibleMethodProvider {
    /**
     * @return returns a map of method names to Method objects.
     */
    Map<String, Method> methods();
}
