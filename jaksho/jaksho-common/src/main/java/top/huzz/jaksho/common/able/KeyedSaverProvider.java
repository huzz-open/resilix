package top.huzz.jaksho.common.able;

import jakarta.annotation.Nonnull;
import lombok.Getter;

import java.util.Map;

/**
 * @author huzz
 * @since 1.0.2
 */
public abstract class KeyedSaverProvider<T, R> implements SaverBuilder<T, R> {
    @Getter
    protected final Map<String, Saver<T, R>> saverMap;

    protected KeyedSaverProvider(Map<String, Saver<T, R>> saverMap) {
        this.saverMap = saverMap;
    }

    @Nonnull
    @Override
    public Saver<T, R> getSaver(T toBeSaved) {
        String key = getKey(toBeSaved);
        return saverMap.get(key);
    }

    @Nonnull
    protected abstract String getKey(T toBeSaved);
}
