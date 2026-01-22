package top.huzz.jaksho.common.constant;

import lombok.Getter;

/**
 * 值字典类型枚举
 *
 * @author huzz
 * @since 1.0.2
 */
@Getter
public enum ValueDictType {
    /**
     * 字符串类型
     */
    STR("字符串型"),

    /**
     * 整型值类型
     */
    INT("整型"),

    /**
     * 字符类型
     */
    CHAR("字符型"),

    /**
     * 浮点类型
     */
    FLOAT("浮点型");

    private final String text;

    ValueDictType(String text) {
        this.text = text;
    }
}


