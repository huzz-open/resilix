package top.huzz.resilix.idempotent;

/**
 * 幂等运行上下文接口
 *
 * @author chenji
 * @since 1.0.0
 */
public interface IdempotentKey {
    /**
     * 获取幂等标识，用于判断是否是同一个任务，相同的值认为是同一个任务
     *
     * @return 幂等标识
     */
    String idempotentKey();
}
