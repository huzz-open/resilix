package top.huzz.resilix.predicate;


import top.huzz.resilix.core.RunContext;
import top.huzz.resilix.recorder.PhaseRecorder;
import top.huzz.resilix.handler.RunHandler;

/**
 * 运行断言器
 * <p>
 * 因为在一个处理链路中，有时候需要根据上下文的不同来判断是否要跳过当前处理器，所以定义了这个接口，举个例子：场景、CASE的执行过程，几乎是一模一样的，除了一些特殊的处理，所以可以通过这个接口来判断是否要跳过当前处理器，实现处理器的复用。
 *
 * @param <C> 上下文类型
 * @author chenji
 * @see PhaseRecorder
 * @see RunHandler
 * @since 1.0.0
 */
public interface HandlerRunPredicate<C extends RunContext> {
    /**
     * 判断是否要执行当前处理器，如果返回false表示不执行，那么就不会执行当前处理器（包含handle、postHandle、finallyHandle这三个方法），且不会被PhaseRecorder记录当前执行器的执行状态
     *
     * @param context 上下文
     * @return true: 运行处理器，false: 不运行处理器
     */
    boolean shouldRun(C context);
}
