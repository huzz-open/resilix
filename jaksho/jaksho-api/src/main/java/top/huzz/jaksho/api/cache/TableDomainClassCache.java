package top.huzz.jaksho.api.cache;

import top.huzz.jaksho.common.entity.BasicProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huzz
 * @since 1.0.2
 */
public final class TableDomainClassCache {
    private static final Map<String, Class<? extends BasicProperties>> CACHE = new HashMap<>();

    /**
     * Retrieves the domain class for the specified tableName.
     *
     * @param tableName the table name
     * @return the corresponding domain class
     * @throws IllegalArgumentException if the tableName is null or empty
     * @throws IllegalStateException    if no domain class is found for the given tableName
     */
    public static Class<? extends BasicProperties> getDomainClass(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty.");
        }
        Class<? extends BasicProperties> domainClass = CACHE.get(tableName);
        if (domainClass == null) {
            throw new IllegalStateException("No domain class found for table name: " + tableName);
        }
        return domainClass;
    }


    static void init(Map<String, Class<? extends BasicProperties>> map) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("Map cannot be null or empty.");
        }
        CACHE.putAll(map);
    }
}
