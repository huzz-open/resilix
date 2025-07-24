package top.huzz.resilix.exception;

import lombok.Getter;
import top.huzz.resilix.idempotent.IdempotentKey;

/**
 * Phase idempotent judgment exception
 *
 * @author chenji
 * @since 1.0.0
 */
@Getter
public class IdempotentJudgeException extends RuntimeException {

    private final IdempotentKey key;

    public IdempotentJudgeException(IdempotentKey key) {
        this.key = key;
    }
}
