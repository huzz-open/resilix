package top.huzz.jaksho.generator;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

/**
 * @author 19796
 * @since 1.0.2
 */
public final class GeneratorUtils {
    public static boolean useCustomJSONHandler(TableField tableField) {
        IColumnType columnType = tableField.getColumnType();
        if (columnType == null) {
            return false;
        }
        if (columnType instanceof CustomColumnType cct) {
            // 非枚举的情况下才使用json序列化器
            return !cct.isEnum();
        }
        return false;
    }
}
