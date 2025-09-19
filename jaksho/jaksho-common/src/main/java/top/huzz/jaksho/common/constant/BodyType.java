package top.huzz.jaksho.common.constant;

/**
 * @author 19796
 * @since 1.0.2
 */
public enum BodyType {
    /**
     * 无请求体
     */
    NONE,
    /**
     * 表单-数据
     */
    FORM_DATA,
    /**
     * 表单-键值对
     */
    FORM_URLENCODED,
    /**
     * RAW - JSON
     */
    RAW_JSON,
    /**
     * RAW - 文本
     */
    RAW_TEXT,
    /**
     * 二进制
     */
    BINARY,

    ;
}
