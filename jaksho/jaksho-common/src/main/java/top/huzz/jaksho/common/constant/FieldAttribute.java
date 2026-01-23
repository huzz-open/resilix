package top.huzz.jaksho.common.constant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import top.huzz.jaksho.common.serializer.FieldAttributeSerializer;

/**
 * 字段属性。
 * <pre>
 * 使用二进制的每一位来表示该字段的属性：
 *   [1]（低1位）：表示该字段是否可以被用于HTTP接口输入参数。1表示可以，0表示不可以。
 *   [2]：表示该字段是否可以被用于HTTP接口输出参数。1表示可以，0表示不可以。
 *   [3]~[8]：预留位。
 * 示例：
 *   "1"表示字段仅仅只能作为接口输入。
 *   "2"表示字段仅仅只能作为接口输出。
 *   "3"表示该字段既可以作为接口输入也可以作为输出。
 * </pre>
 *
 * @author system
 * @since 1.0.3
 */
@Getter
@Setter
@JsonSerialize(using = FieldAttributeSerializer.class)
public class FieldAttribute {

    private static final int INPUT = 0b00000001;   // 1: 输入
    private static final int OUTPUT = 0b00000010;  // 2: 输出

    /**
     * 默认值为3（0b11），表示输入输出均可
     */
    private int value = INPUT | OUTPUT;

    public FieldAttribute() {
    }

    public FieldAttribute(int value) {
        if (value <= 0 || value > 255) {
            throw new IllegalArgumentException(String.format("value must be greater than 0 and less than or equal to %s", 255));
        }
        this.value = value;
    }

    public FieldAttribute(String valueStr) {
        this.value = Integer.parseInt(valueStr);
    }

    /**
     * 根据字符串值构造 FieldAttribute 对象
     */
    public static FieldAttribute of(String valueStr) {
        return new FieldAttribute(valueStr);
    }

    /**
     * 根据整数值构造 FieldAttribute 对象
     */
    public static FieldAttribute of(int value) {
        return new FieldAttribute(value);
    }

    /**
     * 仅输入
     */
    public static FieldAttribute inputOnly() {
        return new FieldAttribute(INPUT);
    }

    /**
     * 仅输出
     */
    public static FieldAttribute outputOnly() {
        return new FieldAttribute(OUTPUT);
    }

    /**
     * 输入输出均可
     */
    public static FieldAttribute both() {
        return new FieldAttribute(INPUT | OUTPUT);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * 是否可作为输入
     */
    public boolean hasInput() {
        return hasAttribute(INPUT);
    }

    /**
     * 是否可作为输出
     */
    public boolean hasOutput() {
        return hasAttribute(OUTPUT);
    }

    /**
     * 检查是否包含指定属性
     */
    private boolean hasAttribute(int flag) {
        return (value & flag) > 0;
    }

    /**
     * 获取属性描述
     */
    public String getDescription() {
        if (value == (INPUT | OUTPUT)) return "输入/输出";
        if (value == INPUT) return "仅输入";
        if (value == OUTPUT) return "仅输出";
        return "未知";
    }
}
