package top.huzz.resilix.evaluation;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chenji
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractReducibleMethodProvider implements ReducibleMethodProvider {
    private final AtomicBoolean hasInitialized = new AtomicBoolean(false);
    protected final Lock lock = new ReentrantLock();
    protected Map<String, Method> methods = new LinkedHashMap<>();

    /**
     * Adds a method to the provider.
     *
     * @param name   the name of the method
     * @param method the method to add
     * @throws IllegalArgumentException if a method with the same name already exists
     */
    protected synchronized void addMethod(String name, Method method) throws IllegalArgumentException {
        try {
            lock.lock();
            if (methods.containsKey(name)) {
                throw new IllegalArgumentException("Method with name '" + name + "' already exists.");
            }
            methods.put(name, method);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Initializes the method provider. This method should be implemented by subclasses
     * to populate the methods map with the appropriate methods.
     */
    protected abstract void initialize();

    @Override
    public Map<String, Method> methods() {
        if (!hasInitialized.getAndSet(true)) {
            initialize();
        }
        return methods;
    }
}
