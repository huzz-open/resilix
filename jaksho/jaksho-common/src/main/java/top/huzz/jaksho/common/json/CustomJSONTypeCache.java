package top.huzz.jaksho.common.json;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenji
 * @since 1.0.2
 */
public final class CustomJSONTypeCache {

    private static final Map<Type, CachePair> CACHE = new HashMap<>();

    private static final int DEF_CACHE_SIZE = 10000;
    private static final int MAX_CACHE_SIZE = cacheMaxSize();

    public static void serializePut(Type type, Object key, String value) {
        CachePair cachePair = cachePair(type);
        cachePair.serializePut(key, value);
    }

    public static String serializeGet(Type type, Object key) {
        CachePair cachePair = CACHE.get(type);
        if (cachePair == null) {
            return null;
        }
        return cachePair.serializeGet(key);
    }

    public static void deserializePut(Type type, String key, Object value) {
        CachePair cachePair = cachePair(type);
        cachePair.deserializePut(key, value);
    }

    public static Object deserializeGet(Type type, String key) {
        CachePair cachePair = CACHE.get(type);
        if (cachePair == null) {
            return null;
        }
        return cachePair.deserializeGet(key);
    }

    private static CachePair cachePair(Type type) {
        CachePair cachePair = CACHE.get(type);
        if (cachePair == null) {
            synchronized (CustomJSONTypeCache.class) {
                cachePair = CACHE.get(type);
                if (cachePair == null) {
                    cachePair = new CachePair(MAX_CACHE_SIZE);
                    CACHE.put(type, cachePair);
                }
            }
        }
        return cachePair;
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

    private static final class CachePair {
        /**
         * key：被序列化的对象
         * Value：序列化后的字符串
         */
        private final Cache<Object, String> serializeCache;

        /**
         * key：序列化后的字符串
         * value：反序列化后的对象
         */
        private final Cache<String, Object> deserializeCache;

        private CachePair(int maxSize) {
            serializeCache = CacheBuilder.newBuilder()
                    .weakValues()
                    .maximumSize(maxSize)
                    .build();
            deserializeCache = CacheBuilder.newBuilder()
                    .weakValues()
                    .maximumSize(maxSize)
                    .build();
        }

        private void serializePut(Object key, String value) {
            serializeCache.put(key, value);
            deserializeCache.put(value, key);
        }

        private String serializeGet(Object key) {
            return serializeCache.getIfPresent(key);
        }

        private void deserializePut(String key, Object value) {
            deserializeCache.put(key, value);
            serializeCache.put(value, key);
        }

        private Object deserializeGet(String key) {
            return deserializeCache.getIfPresent(key);
        }
    }
}
