package top.huzz.jaksho.common.able.exception;

/**
 * @author huzz
 * @since 1.0.2
 */
public class SaveException extends RuntimeException {
    public SaveException() {
    }

    public SaveException(String message) {
        super(message);
    }

    public SaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveException(Throwable cause) {
        super(cause);
    }

    public SaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
