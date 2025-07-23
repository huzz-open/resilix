package top.huzz.resilix.core;

import top.huzz.resilix.idempotent.IdempotentJudge;

/**
 * @author chenji
 * @see IdempotentJudge
 * @since 1.0.0
 */
public interface FinallyHandler<C extends RunContext> {
    /**
     * 在整个处理链路执行完毕后，不管最后的结果是成功还是失败，不管是否重复操作（幂等判断器判断出来是重复操作），一定会执行的逻辑。
     * <pre>
     * 假设有多个阶段，A-B-C-D-E，其中A-B是服务端X的执行阶段，C-D-E是服务端Y的执行阶段，A-B-C-D-E都是串行执行的。
     * 如果需要确保一个finallyHandle被完全执行，那么需要在每个服务的最后一个阶段都调用finallyHandle方法，也就是在B、E阶段调用finallyHandle方法。
     * </pre>
     *
     * @param context 运行上下文
     */
    default void finallyHandle(C context) {
        // do nothing
    }
}
