package top.huzz.resilix.exception.api;

/**
 * @author chenji
 * @since 1.0.0
 */
public class PollUnitException extends WebSocketBaseException {

    public PollUnitException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }
}
