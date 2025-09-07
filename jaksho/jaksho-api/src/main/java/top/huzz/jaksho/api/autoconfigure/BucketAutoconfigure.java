package top.huzz.jaksho.api.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import top.huzz.jaksho.api.cache.AbleCacheRegistryConfiguration;
import top.huzz.jaksho.api.config.AppConfig;

/**
 * @author huzz
 * @since 1.0.2
 */
@Import({
        AbleCacheRegistryConfiguration.class,
        MybatisPlusConfiguration.class,
        CombineResultConfiguration.class
})
@EnableConfigurationProperties(AppConfig.class)
public class BucketAutoconfigure {

}
