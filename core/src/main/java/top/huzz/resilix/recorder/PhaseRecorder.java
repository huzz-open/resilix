package top.huzz.resilix.recorder;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import top.huzz.resilix.core.RunContext;

/**
 * 阶段记录器，每个状态阶段都可以有一个记录器，用于记录阶段的执行结果，比如可以存储到Redis或者数据库中。
 *
 * @author chenji
 * @since 1.0.0
 */
public interface PhaseRecorder<C extends RunContext> {

    /**
     * 阶段开始, 马上要开始执行了
     *
     * @param context 上下文对象
     */
    void readyFor(C context);

    /**
     * 阶段结束
     *
     * @param context 上下文对象
     * @param e       阶段异常
     */
    void end(C context, @Nullable Exception e);

    /**
     * @return 记录器类型
     */
    @Nonnull
    default Type getType() {
        return Type.NOPE;
    }

    enum Type {
        NOPE, PLAN, EXECUTION_UNIT
    }
}
