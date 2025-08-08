package top.huzz.jaksho.api.autoconfigure;

import org.springframework.context.annotation.Import;
import top.huzz.jaksho.api.cache.AbleCacheRegistryConfiguration;
import top.huzz.jaksho.api.domain.MybatisPlusConfiguration;

/**
 * @author huzz
 * @since 1.0.2
 */
@Import({
        AbleCacheRegistryConfiguration.class,
        MybatisPlusConfiguration.class
})
public class BucketAutoconfigure {

}
