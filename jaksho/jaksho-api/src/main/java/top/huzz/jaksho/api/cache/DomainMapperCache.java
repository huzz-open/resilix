package top.huzz.jaksho.api.cache;

import top.huzz.jaksho.common.mapper.ExtBaseMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huzz
 * @since 1.0.2
 */
public final class DomainMapperCache {
	private static final Map<String, ExtBaseMapper<?>> CACHE = new HashMap<>();


	/**
	 * Retrieves the mapper for the specified domainKey.
	 *
	 * @param domainKey the domainKey name
	 * @return the corresponding ExtBaseMapper
	 * @throws IllegalArgumentException if the domainKey is null or empty
	 * @throws IllegalStateException    if no mapper is found for the given domainKey
	 */
	public static ExtBaseMapper<?> getMapper(String domainKey) {
		if (domainKey == null || domainKey.isEmpty()) {
			throw new IllegalArgumentException("Domain cannot be null or empty.");
		}
		ExtBaseMapper<?> mapper = CACHE.get(domainKey);
		if (mapper == null) {
			throw new IllegalStateException("No mapper found for domainKey: " + domainKey);
		}
		return mapper;
	}


	static void init(Map<String, ExtBaseMapper<?>> map) {
		if (map == null || map.isEmpty()) {
			throw new IllegalArgumentException("DomainMapperCache must be initialized with a non-empty map.");
		}
		CACHE.putAll(map);
	}
}
