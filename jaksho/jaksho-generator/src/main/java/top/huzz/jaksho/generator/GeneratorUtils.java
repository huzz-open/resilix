package top.huzz.jaksho.generator;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import top.huzz.jaksho.common.json.CustomJSONType;

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
            // 当列类型是自定义字段类型的情况下，如果带了CustomJSONType注解，则一定使用自定义JSON处理器，否则默认情况就是非枚举的情况下使用
            return cct.getClass().getAnnotation(CustomJSONType.class) != null
                    || !cct.isEnum();
        }
        return false;
    }
}
