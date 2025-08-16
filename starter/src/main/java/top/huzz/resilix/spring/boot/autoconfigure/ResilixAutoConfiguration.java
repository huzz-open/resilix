package top.huzz.resilix.spring.boot.autoconfigure;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.huzz.resilix.core.*;
import top.huzz.resilix.handler.RunHandler;
import top.huzz.resilix.util.ApplicationContextUtils;
import top.huzz.resilix.validation.*;
import top.huzz.resilix.validation.annotation.BizCheckFunction;

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
@Import(ApplicationContextUtils.class)
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

	@Bean(name = "resilixReducibleMethodProvider")
	@ConditionalOnMissingBean
	public ReducibleMethodProvider reducibleMethodProvider() {
		return new DefaultAnnotationReducibleMethodProvider(BizCheckFunction.class);
	}

	@Bean(name = "resilixDBUniqueCheckerProvider")
	@ConditionalOnMissingBean
	public DBUniqueCheckerProvider dbUniqueCheckerProvider(@Value("${resilix.db.unique.checker.enabled:true}") boolean enabled) {
		return new UnsupportedDBUniqueCheckerProvider();
	}

	@Bean(name = "resilixValidations")
	@ConditionalOnMissingBean
	public Validations validations(List<ReducibleMethodProvider> reducibleMethodProviders, Validator validator, DBUniqueCheckerProvider dbUniqueCheckerProvider) {
		return new Validations(reducibleMethodProviders, validator, dbUniqueCheckerProvider);
	}

	@Bean(name = "resilixExpressionConverter")
	@ConditionalOnMissingBean
	public ExpressionConverter expressionConverter() {
		return new ShortFunctionCallConverter();
	}

	@Bean(name = "resilixvalidator")
	@ConditionalOnMissingBean(Validator.class)
	public Validator validator() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			return factory.getValidator();
		}
	}
}
