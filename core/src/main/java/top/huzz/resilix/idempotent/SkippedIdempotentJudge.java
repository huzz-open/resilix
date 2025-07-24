package top.huzz.resilix.idempotent;


import top.huzz.resilix.exception.IdempotentJudgeException;

/**
 * Marker interface to identify a state: when the previous phase is skipped due to idempotent judgment,
 * the subsequent phases should also be skipped, implemented by throwing exception {@link IdempotentJudgeException}
 *
 * @author chenji
 * @see IdempotentJudgeException
 * @since 1.0.0
 */
public interface SkippedIdempotentJudge {
}
