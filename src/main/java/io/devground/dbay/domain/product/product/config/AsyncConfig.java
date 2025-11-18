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
		// TODO: 우선 20개로 처리 (인덱스 추가 작업이 많지는 않을 것으로 예상). 모니터링 후 개선
		int maxPollSize = coreSize * 20;

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
