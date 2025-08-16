package top.huzz.jaksho.api.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import top.huzz.jaksho.common.able.DomainBasedSaverProvider;
import top.huzz.jaksho.common.able.DomainDescription;
import top.huzz.jaksho.common.able.Saver;
import top.huzz.jaksho.common.able.SaverBuilder;
import top.huzz.jaksho.common.mapper.ExtBaseMapper;
import top.huzz.resilix.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huzz
 * @since 1.0.2
 */
public class AbleCacheRegistryConfiguration {

	private static final String MAPPER_SUFFIX = "Mapper";

	@Bean
	@ConditionalOnMissingBean
	public DomainMapperTrans<ExtBaseMapper<?>> domainMapperTrans() {
		return domainClass -> {
			String mapperClassName = domainClass.getName().replace("entity", "mapper") + MAPPER_SUFFIX;
			return ReflectionUtils.forName(mapperClassName);
		};
	}

	@Bean
	@ConditionalOnMissingBean
	public SaverBuilder<? extends DomainDescription, ?> domainBasedSaverProvider(ApplicationContext applicationContext, DomainMapperTrans<ExtBaseMapper<?>> domainMapperTrans) {
		DomainBasedSaverProvider<? extends DomainDescription, ?> domainBasedSaverProvider = new DomainBasedSaverProvider<>(new HashMap<>());
		Map<String, ? extends Saver<? extends DomainDescription, ?>> saverMap = domainBasedSaverProvider.getSaverMap();

		DomainDescriptionAbleCacheInitializer ableCacheInitializer = new DomainDescriptionAbleCacheInitializer(
				applicationContext,
				domainMapperTrans,
				saverMap,
				DomainDescription.class,
				"top.huzz.jaksho.domain"
		);
		ableCacheInitializer.init();

		DomainMapperCache.init(ableCacheInitializer.mapperMap);

		return domainBasedSaverProvider;
	}


	static class DomainDescriptionAbleCacheInitializer extends AbleCacheInitializer<DomainDescription> {
		private final Map<String, ExtBaseMapper<?>> mapperMap = new HashMap<>();
		private final ApplicationContext applicationContext;
		private final DomainMapperTrans<ExtBaseMapper<?>> domainMapperTrans;

		public DomainDescriptionAbleCacheInitializer(ApplicationContext applicationContext, DomainMapperTrans<ExtBaseMapper<?>> domainMapperTrans,
													 Map<String, ? extends Saver<? extends DomainDescription, ?>> saverMap, Class<DomainDescription> ableClass, String... packagesToScan) {
			super(saverMap, ableClass, packagesToScan);
			this.applicationContext = applicationContext;
			this.domainMapperTrans = domainMapperTrans;
		}

		@Override
		protected Object buildKey(DomainDescription instanceAble) {
			return instanceAble.name();
		}

		@Override
		protected Object buildValue(DomainDescription instanceAble) {
			Class<ExtBaseMapper<?>> baseMapperClass = domainMapperTrans.trans(instanceAble.getClass());
			ExtBaseMapper<?> extBaseMapper = applicationContext.getBean(baseMapperClass);
			mapperMap.put(instanceAble.name(), extBaseMapper);
			return extBaseMapper;
		}
	}
}
