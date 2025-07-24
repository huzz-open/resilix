package top.huzz.resilix.exception.api;

/**
 * API connection exception
 *
 * @author chenji
 * @since 1.0.0
 */
public class ApiConnectException extends WebSocketBaseException {
    public ApiConnectException(String message) {
        super(message);
    }

    public ApiConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
