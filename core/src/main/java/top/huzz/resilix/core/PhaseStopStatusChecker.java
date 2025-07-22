package top.huzz.resilix.core;


import top.huzz.resilix.exception.PhaseStoppedException;

/**
 * 状态的停止状态检查器
 *
 * @author chenji
 * @since 1.0.0
 */
public interface PhaseStopStatusChecker {

    /**
     * 检查状态是否停止
     *
     * @param context 上下文
     * @throws PhaseStoppedException 当状态停止时抛出异常，执行管理器应当处理这个抛出的异常，以便对停止状态进行处理
     */
    void check(RunContext context) throws PhaseStoppedException;
}
