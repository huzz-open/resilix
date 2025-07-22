package top.huzz.resilix.exception.api;

/**
 * 连接异常
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
