package top.huzz.jaksho.api.domain;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author huzz
 * @since 1.0.2
 */
public class MybatisPlusConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler customMetaObjectHandler() {
        return new CustomMetaObjectHandler();
    }
}
