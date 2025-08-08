package top.huzz.jaksho.common.able;

import jakarta.annotation.Nonnull;

/**
 * @param <T> 被保存的对象类型
 * @param <R> 保存结果类型
 * @author huzz
 * @since 1.0.2
 */
public interface SaverBuilder<T, R> {
    /**
     * 获取保存器
     *
     * @param toBeSaved 被保存的对象
     */
    @Nonnull
    Saver<T, R> getSaver(T toBeSaved);
}
