package top.huzz.jaksho.common.able;

import top.huzz.jaksho.common.able.exception.SaveException;

/**
 * @param <T> 被保存的对象类型
 * @param <R> 保存结果类型
 * @author huzz
 * @since 1.0.2
 */
public interface Saver<T, R> {

    /**
     * 保存对象
     *
     * @param object 被保存的对象
     * @throws SaveException 保存异常
     */
    R save(T object) throws SaveException;
}
