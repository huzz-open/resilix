package top.huzz.jaksho.api.cache;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import top.huzz.jaksho.api.config.AppConfig;
import top.huzz.jaksho.common.able.DomainBasedSaverProvider;
import top.huzz.jaksho.common.able.Saver;
import top.huzz.jaksho.common.able.SaverBuilder;
import top.huzz.jaksho.common.entity.BasicProperties;
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
    public SaverBuilder<? extends BasicProperties, ?> domainBasedSaverProvider(ApplicationContext applicationContext,
                                                                               DomainMapperTrans<ExtBaseMapper<?>> domainMapperTrans,
                                                                               AppConfig appConfig) {
        DomainBasedSaverProvider<? extends BasicProperties, ?> domainBasedSaverProvider = new DomainBasedSaverProvider<>(new HashMap<>());
        Map<String, ? extends Saver<? extends BasicProperties, ?>> saverMap = domainBasedSaverProvider.getSaverMap();

        BasicPropertiesAbleCacheInitializer ableCacheInitializer = new BasicPropertiesAbleCacheInitializer(
                applicationContext,
                domainMapperTrans,
                saverMap,
                BasicProperties.class,
                appConfig.getPackages().getDomainBase()
        );
        ableCacheInitializer.init();

        // Initialize caches
        DomainMapperCache.init(ableCacheInitializer.mapperMap);
        TableDomainClassCache.init(ableCacheInitializer.tableNameDomainClassMap);

        return domainBasedSaverProvider;
    }


    static class BasicPropertiesAbleCacheInitializer extends AbleCacheInitializer<BasicProperties> {
        private final Map<String, ExtBaseMapper<?>> mapperMap = new HashMap<>();
        private final Map<String, Class<? extends BasicProperties>> tableNameDomainClassMap = new HashMap<>();
        private final ApplicationContext applicationContext;
        private final DomainMapperTrans<ExtBaseMapper<?>> domainMapperTrans;

        public BasicPropertiesAbleCacheInitializer(ApplicationContext applicationContext,
                                                   DomainMapperTrans<ExtBaseMapper<?>> domainMapperTrans,
                                                   Map<String, ? extends Saver<? extends BasicProperties, ?>> saverMap,
                                                   Class<BasicProperties> ableClass, String... packagesToScan) {
            super(saverMap, ableClass, packagesToScan);
            this.applicationContext = applicationContext;
            this.domainMapperTrans = domainMapperTrans;
        }

        @Override
        protected Object buildKey(BasicProperties instanceAble) {
            return instanceAble.name();
        }

        @Override
        protected Object buildValue(BasicProperties instanceAble) {
            Class<? extends BasicProperties> instanceAbleClass = instanceAble.getClass();
            Class<ExtBaseMapper<?>> baseMapperClass = domainMapperTrans.trans(instanceAbleClass);
            ExtBaseMapper<?> extBaseMapper = applicationContext.getBean(baseMapperClass);
            if (mapperMap.put(instanceAble.name(), extBaseMapper) != null) {
                throw new IllegalStateException("Duplicate mapper for " + instanceAble.name());
            }

            if (tableNameDomainClassMap.put(getTableNameByDomainClass(instanceAbleClass), instanceAbleClass) != null) {
                throw new IllegalStateException("Duplicate table name for " + instanceAbleClass.getName());
            }
            return extBaseMapper;
        }

        private String getTableNameByDomainClass(Class<? extends BasicProperties> domainClass) {
            TableName annotation = domainClass.getAnnotation(TableName.class);
            if (annotation != null) {
                return annotation.value();
            }
            return StringUtils.camelToUnderline(domainClass.getSimpleName());
        }
    }
}
