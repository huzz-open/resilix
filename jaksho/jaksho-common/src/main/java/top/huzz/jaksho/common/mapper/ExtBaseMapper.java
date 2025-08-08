package top.huzz.jaksho.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.huzz.jaksho.common.able.DomainDescription;
import top.huzz.jaksho.common.able.Saver;
import top.huzz.jaksho.common.able.exception.SaveException;

/**
 * @param <T> 实体类型
 * @author huzz
 * @since 1.0.2
 */
public interface ExtBaseMapper<T extends DomainDescription> extends BaseMapper<T>, Saver<T, Integer> {

    @Override
    default Integer save(T object) throws SaveException {
        return insert(object);
    }
}
