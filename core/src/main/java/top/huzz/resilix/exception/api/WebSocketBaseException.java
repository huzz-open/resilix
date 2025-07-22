package top.huzz.resilix.exception.api;

/**
 * @author chenji
 * @since 1.0.0
 */
public class WebSocketBaseException extends RuntimeException {
    public WebSocketBaseException() {
    }

    public WebSocketBaseException(String message) {
        super(message);
    }

    public WebSocketBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
