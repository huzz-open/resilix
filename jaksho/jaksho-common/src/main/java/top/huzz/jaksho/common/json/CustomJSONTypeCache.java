package top.huzz.jaksho.common.json;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author chenji
 * @since 1.0.2
 */
public final class CustomJSONTypeCache {

    /**
     * key：被序列化的对象
     * Value：序列化后的字符串
     */
    private static final Cache<Object, String> serializeCache;

    /**
     * key：序列化后的字符串
     * value：反序列化后的对象
     */
    private static final Cache<String, Object> deserializeCache;

    private static final int DEF_CACHE_SIZE = 10000;

    static {
        int maxSize = cacheMaxSize();
        serializeCache = CacheBuilder.newBuilder()
                .weakValues()
                .maximumSize(maxSize)
                .build();

        deserializeCache = CacheBuilder.newBuilder()
                .weakValues()
                .maximumSize(maxSize)
                .build();
    }

    static void serializePut(Object key, String value) {
        serializeCache.put(key, value);
        deserializeCache.put(value, key);
    }

    static String serializeGet(Object key) {
        return serializeCache.getIfPresent(key);
    }

    static void deserializePut(String key, Object value) {
        deserializeCache.put(key, value);
        serializeCache.put(value, key);
    }

    static Object deserializeGet(String key) {
        return deserializeCache.getIfPresent(key);
    }

    private static int cacheMaxSize() {
        int defVal = DEF_CACHE_SIZE;
        String maxSizeStr = System.getProperty("cache.classEnum.maxSize");
        if (StringUtils.isBlank(maxSizeStr)) {
            return defVal;
        }
        try {
            return Integer.parseInt(maxSizeStr);
        } catch (Exception e) {
            return defVal;
        }
    }
}
