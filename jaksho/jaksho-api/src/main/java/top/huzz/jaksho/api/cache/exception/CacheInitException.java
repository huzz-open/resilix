package top.huzz.jaksho.api.cache.exception;

/**
 * @author huzz
 * @since 1.0.2
 */
public class CacheInitException extends RuntimeException {
    protected final String cacheType;

    public CacheInitException(String cacheType, String message, Throwable cause) {
        super(message, cause);
        this.cacheType = cacheType;
    }
}
