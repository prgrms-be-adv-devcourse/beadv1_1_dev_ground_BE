package io.devground.product.product.infrastructure.config.elasticsearch;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class VectorIndexAsyncConfig {

	@Bean(name = "vectorTaskExecutor")
	public Executor vectorTaskExecutor() {

		int coreSize = Runtime.getRuntime().availableProcessors();
		// TODO: 우선 coreSize * 2개로 처리. 모니터링 후 개선
		int maxPoolSize = coreSize * 2;

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(coreSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(200);
		executor.setThreadNamePrefix("Vector-Index-");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(30);
		executor.initialize();

		return executor;
	}
}
