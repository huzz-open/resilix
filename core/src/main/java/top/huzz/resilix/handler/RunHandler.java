package top.huzz.resilix.handler;


import jakarta.annotation.Nonnull;
import top.huzz.resilix.predicate.AlwaysRunPredicate;
import top.huzz.resilix.predicate.HandlerRunPredicate;
import top.huzz.resilix.recorder.NopePhaseRecorder;
import top.huzz.resilix.recorder.PhaseRecorder;
import top.huzz.resilix.core.FinallyHandler;
import top.huzz.resilix.core.Phase;
import top.huzz.resilix.core.RunContext;

import java.util.List;

/**
 * 运行处理器接口
 *
 * @param <C> 上下文类型
 * @author chenji
 * @since 1.0.0
 */
public interface RunHandler<C extends RunContext> extends FinallyHandler<C> {
    /**
     * 处理器核心运行逻辑
     *
     * @param context 上下文
     * @throws Exception 当处理过程出现了不可忽略的异常时，抛出异常，此时认为处理结果是失败的，就不会继续往下执行下一个处理器
     */
    void handle(C context) throws Exception;

    /**
     * @return 处理器可以处理的测试计划运行阶段
     */
    @Nonnull
    Phase phase();

    /**
     * 单个RunHandler的后置处理，不管handle方法是否抛出异常，都会执行
     *
     * @param context 运行上下文
     */
    default void postHandle(C context) {
        // do nothing
    }

    /**
     * @return 阶段记录器
     */
    @SuppressWarnings("unchecked")
    default PhaseRecorder<C> getRecorder() {
        return (PhaseRecorder<C>) new NopePhaseRecorder();
    }

    /**
     * @return 运行断言器类型列表
     */
    @Nonnull
    default List<HandlerRunPredicate<C>> runPredicate() {
        return List.of(new AlwaysRunPredicate<>());
    }
}
