package top.huzz.resilix.exception;

import lombok.Getter;

/**
 * new an instance exception.
 *
 * @author chenji
 * @since 1.0.0
 */
@Getter
public class NewInstanceException extends RuntimeException {
    private final String className;

    public NewInstanceException(String clazz, Throwable cause) {
        super(cause);
        this.className = clazz;
    }

    public NewInstanceException(Class<?> clazz, Throwable cause) {
        this(clazz.getName(), cause);
    }
}
