package top.huzz.jaksho.api.cache;

import top.huzz.jaksho.api.cache.exception.CacheInitException;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface CacheInitializer {
    /**
     * 初始化自动缓存
     * <p>
     * 该方法会在应用启动时被调用，用于初始化自动缓存。
     * </p>
     *
     * @throws CacheInitException 如果初始化失败
     */
    void init() throws CacheInitException;
}
