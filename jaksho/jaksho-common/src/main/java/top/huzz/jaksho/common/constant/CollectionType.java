package top.huzz.jaksho.common.constant;

import lombok.Getter;

/**
 * @author chenji
 * @since 1.0.2
 */
@Getter
public enum CollectionType {
    /**
     * 非集合类型
     */
    NONE("非集合类型"),

    /**
     * 列表类型，可以有重复元素，有序。
     */
    LIST("有序列表"),

    /**
     * 集合类型，无重复元素，无序。
     */
    SET("无序列表"),

    /**
     * 数组类型，可以有重复元素，有序。
     */
    ARRAY("数组");

    CollectionType(String text) {
        this.text = text;
    }

    private final String text;
}
