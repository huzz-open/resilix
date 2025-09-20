package top.huzz.jaksho.common.entity;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 通用多表查询结果容器
 * 用于存储多表连接查询的结果，每个表的数据以表名为key存储在domains中
 *
 * @author huzz
 * @since 1.0.2
 */
@Getter
@Setter
public class CombineResult {
    public static final String FIELD_DOMAINS = "domains";
    /**
     * 存储多表查询结果
     * key: 表名
     * value: 对应的实体对象
     */
    private final Map<String, BasicProperties> domains = new LinkedHashMap<>();

    @Nonnull
    public <T> BasicProperties get(Class<T> domainClass) {
        return domains
                .values()
                .stream()
                .filter(domain -> domainClass.isAssignableFrom(domain.getClass()))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No domain found for class: " + domainClass.getName())
                );
    }

    @SuppressWarnings("unchecked")
    public <T> T get() {
        if (domains.size() != 1) {
            throw new IllegalStateException("Cannot determine single domain when multiple domains exist.");
        }
        return (T) domains.values().iterator().next();
    }

    public CombineResult put(String domainName, BasicProperties domain) {
        this.domains.put(domainName, domain);
        return this;
    }

    public static CombineResult of(String domainName1, BasicProperties domain1) {
        CombineResult combineResult = new CombineResult();
        combineResult.getDomains().put(domainName1, domain1);
        return combineResult;
    }

    public static CombineResult of(String domainName1, BasicProperties domain1, String domainName2, BasicProperties domain2) {
        return of(domainName1, domain1).put(domainName2, domain2);
    }
}
