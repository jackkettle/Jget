package com.jget.core.configuration;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@PropertySources({
	@PropertySource(value = "classpath:user-config.properties", ignoreResourceNotFound=true),
	@PropertySource("classpath:config.properties")
})
public class SpringConfiguration
		extends WebMvcConfigurerAdapter {

	@Value("${batch.max.pool.size:5}")
	private int batchMaxPoolSize;

	@Resource
	public Environment environment;

	@Bean
	public ConfigurationManager configurationManager () {
		ConfigurationManager configurationManager = new ConfigurationManager ();
		configurationManager.init (environment);
		return configurationManager;
	}
	
	@Bean
	public ThreadPoolTaskExecutor taskExecutor () {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor ();
		taskExecutor.setMaxPoolSize (batchMaxPoolSize);
		taskExecutor.afterPropertiesSet ();
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		return taskExecutor;
	}

	@Override
	public void addResourceHandlers (ResourceHandlerRegistry registry) {
		registry.addResourceHandler ("/**").addResourceLocations ("/dist/").setCachePeriod (0);
	}

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger (SpringConfiguration.class);

}
