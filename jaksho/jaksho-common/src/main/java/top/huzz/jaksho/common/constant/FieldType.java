package top.huzz.jaksho.common.constant;

/**
 * @author 19796
 * @since 1.0.2
 */
public enum FieldType {
    /**
     * Query字段，也就是拼接在url后面的参数
     */
    QUERY,
    /**
     * Path字段，也就是路径参数，比如: /api/user/{id} 里面的id
     */
    PATH,
    /**
     * 表单-数据里面的字段
     */
    FORM_DATA,
    /**
     * 表单-键值对里面的字段
     */
    FORM_URLENCODED,

    /**
     * RAW - JSON 里面的字段
     */
    RAW_JSON,

    /**
     * RAW - 文本 里面的字段
     */
    RAW_TEXT,

    ;
}
