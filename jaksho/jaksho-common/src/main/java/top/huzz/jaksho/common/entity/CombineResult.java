package top.huzz.jaksho.common.entity;

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
    /**
     * 存储多表查询结果
     * key: 表名
     * value: 对应的实体对象
     */
    private final Map<String, Object> domains = new LinkedHashMap<>();
}
