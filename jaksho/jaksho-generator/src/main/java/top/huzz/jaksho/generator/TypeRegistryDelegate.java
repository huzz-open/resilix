package top.huzz.jaksho.generator;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.type.TypeRegistry;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 类型注册器的代理类, 主要用于给特定的数据库表字段生成自己指定的类型
 *
 * @author chenji
 * @since 1.0.2
 */
public class TypeRegistryDelegate {

    static TypeRegistry typeRegistry;

    /**
     * 扩展类型映射
     * key: tableName+columnName
     * value: ColumnType
     */
    public static Map<String, IColumnType> extTypeMap = new HashMap<>();

    public TypeRegistryDelegate(TypeRegistry typeRegistry) {
        TypeRegistryDelegate.typeRegistry = typeRegistry;
        try {
            initExtTypeMap();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize extended type map", e);
        }
    }

    public IColumnType getColumnType(TableField.MetaInfo metaInfo) {
        String columnName = metaInfo.getColumnName();
        String key = calcKey(metaInfo.getTableName(), columnName);
        return extTypeMap.getOrDefault(columnName,
                extTypeMap.getOrDefault(key, typeRegistry.getColumnType(metaInfo)));
    }

    public static String calcKey(@Nullable String tableName, @Nonnull String columnName) {
        Objects.requireNonNull(columnName);
        return tableName + "." + columnName;
    }

    private static void initExtTypeMap() {
        Settings.tableFiledTypeMap.forEach((key, clazz) ->
                extTypeMap.put(key, new IColumnType() {
                    @Override
                    public String getType() {
                        return clazz.getSimpleName();
                    }

                    @Override
                    public String getPkg() {
                        return clazz.getCanonicalName();
                    }
                })
        );
    }

}
