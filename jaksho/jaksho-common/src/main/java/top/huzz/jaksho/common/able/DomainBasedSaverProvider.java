package top.huzz.jaksho.common.able;

import jakarta.annotation.Nonnull;

import java.util.Map;

/**
 * 基于域名的保存器提供者
 *
 * @author huzz
 * @since 1.0.2
 */
public class DomainBasedSaverProvider<T extends DomainDescription, R> extends KeyedSaverProvider<T, R> {

    public DomainBasedSaverProvider(Map<String, Saver<T, R>> saverMap) {
        super(saverMap);
    }

    @Nonnull
    @Override
    protected String getKey(T builderParam) {
        return builderParam.name();
    }
}
