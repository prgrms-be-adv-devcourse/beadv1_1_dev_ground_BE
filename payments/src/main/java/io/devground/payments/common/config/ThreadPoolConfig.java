package io.devground.payments.common.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ThreadPoolConfig {

	private final int coreSize = Runtime.getRuntime().availableProcessors();

	@Bean
	public Executor taskExecutor() {

		// TODO: 정산 배치, 인덱싱 등 추후 적지 않게 사용할 수 있을 것으로 예상. 모니터링을 통한 스케일링 필요
		int maxPoolSize = coreSize * 5;

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(coreSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("async-");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(30);
		executor.initialize();

		return executor;
	}

	@Bean
	public ThreadPoolTaskScheduler taskScheduler() {

		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

		// TODO: 정산, 배치가 많아질 거 같아서 우선 coreSize만큼 늘림. 추후 모니터링을 통한 스케일링 진행 예정
		scheduler.setPoolSize(coreSize);
		scheduler.setThreadNamePrefix("scheduler-");
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		scheduler.setAwaitTerminationSeconds(30);

		return scheduler;
	}
}
