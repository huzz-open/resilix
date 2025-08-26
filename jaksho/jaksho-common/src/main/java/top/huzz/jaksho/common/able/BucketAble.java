package top.huzz.jaksho.common.able;

import lombok.AllArgsConstructor;

/**
 * @author chenji
 * @since 1.0.0
 */

@AllArgsConstructor
@SuppressWarnings("rawtypes")
public final class BucketAble {
    private final SaverBuilder saverBuilder;

    @SuppressWarnings("unchecked")
    public <T, R> SaverBuilder<T, R> getSaverBuilder() {
        return saverBuilder;
    }
}
