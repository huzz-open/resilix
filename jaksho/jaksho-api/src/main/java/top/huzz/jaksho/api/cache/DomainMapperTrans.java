package top.huzz.jaksho.api.cache;

import top.huzz.jaksho.common.able.DomainDescription;
import top.huzz.jaksho.common.mapper.ExtBaseMapper;

/**
 * @param <M> the type of the domain mapper
 * @author huzz
 * @since 1.0.2
 */
@FunctionalInterface
public interface DomainMapperTrans<M extends ExtBaseMapper<?>> {
    Class<M> trans(Class<? extends DomainDescription> domainClass);
}
