package com.ap.greenpole.stockbroker.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {
	
	@Value("${executor.pool.size}")
	int executorPoolSize;

	@Bean
	public ThreadPoolExecutor getAsyncExecutor() {
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(executorPoolSize);
	}
}
