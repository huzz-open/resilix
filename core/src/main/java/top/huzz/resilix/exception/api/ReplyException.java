package top.huzz.resilix.exception.api;

/**
 * @author chenji
 * @since 1.0.0
 */
public class ReplyException extends WebSocketBaseException {
    public ReplyException(String message) {
        super(message);
    }

    public ReplyException(String message, Throwable cause) {
        super(message, cause);
    }
}
