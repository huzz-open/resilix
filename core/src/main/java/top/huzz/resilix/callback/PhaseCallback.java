package top.huzz.resilix.callback;


import jakarta.annotation.Nullable;
import top.huzz.resilix.core.RunContext;

/**
 * 阶段回调接口
 *
 * @author chenji
 * @since 1.0.0
 */
public interface PhaseCallback {
    /**
     * 异步阶段回调
     *
     * @param context 运行上下文
     * @param extra   附加信息
     */
    void callback(RunContext context, @Nullable Object extra);
}
