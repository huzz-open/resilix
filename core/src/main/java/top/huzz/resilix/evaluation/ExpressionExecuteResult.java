package top.huzz.resilix.evaluation;

import jakarta.annotation.Nullable;
import lombok.Getter;

/**
 * @author huzz
 * @since 1.0.2
 */
public class ExpressionExecuteResult {
    @Getter
    // 原始的返回结果
    private final Object origin;

    public ExpressionExecuteResult(@Nullable Object origin) {
        this.origin = origin;
    }


    /**
     * 正向结果
     *
     * @return true；正向结果
     * @see top.huzz.resilix.annotation.BizCheck
     */
    public boolean isPositive() {
        if (origin == null) {
            // 对执行结果来说，不抛出错误是正向结果
            return true;
        }
        // 不为false、0、"0"、'0'即可认为是正向结果
        return !(origin.equals(false) || origin.equals(0) || origin.equals("0") || origin.equals('0'));
    }

    /**
     * 负向结果
     *
     * @return true；负向结果
     * @see top.huzz.resilix.annotation.BizCheck
     */
    public boolean isNegative() {
        return !isPositive();
    }
}
