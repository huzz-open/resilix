package top.huzz.jaksho.common.exhibition;

import lombok.Getter;

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

    @Getter
    enum Builtin implements Tip {
        OK(0, "ok"),
        ;
        private final long code;
        private final String message;


        Builtin(long code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
