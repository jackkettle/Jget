package com.jget.core.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class SpringConfiguration {

    @Value("${batch.max.pool.size:5}")
    private int batchMaxPoolSize;;
    
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(batchMaxPoolSize);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
    
}
