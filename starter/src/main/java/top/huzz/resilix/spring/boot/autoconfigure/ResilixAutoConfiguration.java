package top.huzz.resilix.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.huzz.resilix.core.*;
import top.huzz.resilix.handler.RunHandler;

import java.util.List;

/**
 * Auto-configuration class for Resilix framework integration with Spring Boot.
 * This configuration automatically sets up the necessary beans for Resilix
 * to work in a Spring Boot application.
 *
 * @author chenji
 * @since 1.0.0
 */
@Configuration
public class ResilixAutoConfiguration {

    /**
     * Creates a RunHandlerCache bean that manages all RunHandler instances.
     *
     * @param runHandlers list of all RunHandler instances found in the application context
     * @return a configured RunHandlerCache instance
     */
    @Bean(name = "resilixRunHandlerCache")
    @ConditionalOnMissingBean
    public RunHandlerCache runHandlerCache(List<RunHandler<? extends RunContext>> runHandlers) {
        return new RunHandlerCache(runHandlers);
    }

    /**
     * Creates a RunHandlerManagerFactory bean using the provided RunHandlerCache.
     *
     * @param runHandlerCache the RunHandlerCache to use for handler management
     * @return a configured DefaultRunHandlerManagerFactory instance
     */
    @Bean(name = "resilixRunHandlerManagerFactory")
    @ConditionalOnMissingBean
    public RunHandlerManagerFactory runHandlerManagerFactory(RunHandlerCache runHandlerCache) {
        DefaultRunHandlerManagerFactory managerFactory = new DefaultRunHandlerManagerFactory(runHandlerCache);
        // Initialize the RunHandlerManagerHelper with the factory instance
        RunHandlerManagerHelper.setRunHandlerManagerFactory(managerFactory);
        return managerFactory;
    }
}
