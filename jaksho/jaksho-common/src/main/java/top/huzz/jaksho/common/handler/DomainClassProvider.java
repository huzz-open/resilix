package top.huzz.jaksho.common.handler;

import jakarta.annotation.Nonnull;
import top.huzz.jaksho.common.entity.BasicProperties;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface DomainClassProvider {
    /**
     * Retrieves the domain class for the specified tableName.
     *
     * @param tableName the table name
     * @return the corresponding domain class
     */
    @Nonnull
    Class<? extends BasicProperties> getDomainClass(String tableName);
}
