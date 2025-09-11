package top.huzz.jaksho.common.exhibition;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface Tip {
    /**
     * @return 状态码
     */
    long getCode();

    /**
     * @return 提示信息
     */
    String getMessage();
}
