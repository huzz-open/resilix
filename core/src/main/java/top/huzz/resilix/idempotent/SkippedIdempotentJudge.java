package top.huzz.resilix.idempotent;


import top.huzz.resilix.exception.IdempotentJudgeException;

/**
 * 标识接口，用于标识一种状态：当前面的阶段因为幂等判断而被跳过后，接下来的阶段也应该被跳过，通过抛出异常{@link IdempotentJudgeException}的方式来实现
 *
 * @author chenji
 * @see IdempotentJudgeException
 * @since 1.0.0
 */
public interface SkippedIdempotentJudge {
}
