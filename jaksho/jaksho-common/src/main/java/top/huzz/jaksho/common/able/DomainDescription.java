package top.huzz.jaksho.common.able;

import jakarta.annotation.Nonnull;

/**
 * @author huzz
 * @since 1.0.2
 */
public interface DomainDescription extends Able {
    /**
     * 获取域名描述的值，需全局唯一，默认返回class的全限定名
     *
     * @return 域名描述的值
     */
    @Nonnull
    default String name() {
        return getClass().getName();
    }
}
