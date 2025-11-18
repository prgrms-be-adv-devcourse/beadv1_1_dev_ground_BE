package io.devground.dbay.domain.product.product.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

	@Bean(name = "esTaskExecutor")
	public Executor esTaskExecutor() {

		int coreSize = Runtime.getRuntime().availableProcessors();
		// TODO: CPU-Bound 작업이 많을 것으로 예상. 모니터링 후 개선
		int maxPollSize = coreSize * 2;

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(coreSize);
		executor.setMaxPoolSize(maxPollSize);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("ES-Index-");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());    // 큐 초과 시 호출 스레드에서 실행
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(30);
		executor.initialize();

		return executor;
	}
}
