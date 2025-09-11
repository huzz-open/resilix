package top.huzz.jaksho.common.entity;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import top.huzz.jaksho.common.exhibition.Tip;

/**
 * 统一响应结果
 *
 * @param <T> 数据类型
 * @author huzz
 * @since 1.0.2
 */
@Getter
@Setter
public final class Resp<T> {
    private T data;
    private long code;
    private String message;

    private Resp() {
    }

    public static <T> Resp<T> of(T data) {
        return of(data, Tip.Builtin.OK);
    }

    public static <T> Resp<T> of(Tip tip) {
        return of(null, tip.getCode(), tip.getMessage());
    }

    public static <T> Resp<T> of(T data, Tip tip) {
        return of(data, tip.getCode(), tip.getMessage());
    }

    private static <T> Resp<T> of(@Nullable T data, long code, String message) {
        Resp<T> resp = new Resp<>();
        resp.setData(data);
        resp.setCode(code);
        resp.setMessage(message);
        return resp;
    }
}
