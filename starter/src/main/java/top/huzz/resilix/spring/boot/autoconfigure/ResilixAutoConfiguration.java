package top.huzz.resilix.spring.boot.autoconfigure;

import top.huzz.resilix.core.DefaultRunHandlerManagerFactory;
import top.huzz.resilix.core.RunContext;
import top.huzz.resilix.core.RunHandlerCache;
import top.huzz.resilix.core.RunHandlerManagerFactory;
import top.huzz.resilix.handler.RunHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author chenji
 * @since 1.0.0
 */
@Configuration
public class ResilixAutoConfiguration {

    @Bean(name = "resilixRunHandlerCache")
    @ConditionalOnMissingBean
    public RunHandlerCache runHandlerCache(List<RunHandler<? extends RunContext>> runHandlers) {
        return new RunHandlerCache(runHandlers);
    }

    @Bean(name = "resilixRunHandlerManagerFactory")
    @ConditionalOnMissingBean
    public RunHandlerManagerFactory runHandlerManagerFactory(RunHandlerCache runHandlerCache) {
        return new DefaultRunHandlerManagerFactory(runHandlerCache);
    }

}
