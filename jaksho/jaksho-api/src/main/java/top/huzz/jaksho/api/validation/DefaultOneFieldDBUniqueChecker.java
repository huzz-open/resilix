package top.huzz.jaksho.api.validation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Nonnull;
import top.huzz.jaksho.api.cache.DomainMapperCache;
import top.huzz.resilix.validation.AbstractOneFieldDBUniqueChecker;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of a database uniqueness checker for a single field.
 * <p>
 * based on MyBatis-Plus framework.
 * </p>
 *
 * @author huzz
 * @since 1.0.2
 */
public class DefaultOneFieldDBUniqueChecker extends AbstractOneFieldDBUniqueChecker {

	private static final Map<String, BaseMapper<?>> MAPPER_MAP = new HashMap<>();

	public DefaultOneFieldDBUniqueChecker(String domainKey, @Nonnull String field) {
		super(domainKey, field);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected boolean exists(String domainKey, String field, Object value) {
		BaseMapper baseMapper = getMapper(domainKey);
		QueryWrapper<Object> query = Wrappers.query();
		query.eq(field, value);
		return baseMapper.exists(query);
	}

	private static BaseMapper<?> getMapper(String domainKey) {
		BaseMapper<?> baseMapper = MAPPER_MAP.get(domainKey);
		if (baseMapper == null) {
			synchronized (MAPPER_MAP) {
				baseMapper = MAPPER_MAP.get(domainKey);
				if (baseMapper == null) {
					baseMapper = createMapper(domainKey);
					MAPPER_MAP.put(domainKey, baseMapper);
				}
			}
		}
		return baseMapper;
	}

	private static BaseMapper<?> createMapper(String domainKey) {
		return DomainMapperCache.getMapper(domainKey);
	}
}
