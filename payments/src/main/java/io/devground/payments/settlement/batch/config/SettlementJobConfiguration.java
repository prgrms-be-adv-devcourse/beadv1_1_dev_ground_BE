package io.devground.payments.settlement.batch.config;

import java.util.UUID;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * 정산 배치 Job 구성
 * 2주 지난 확정 주문에 대한 정산 처리 및 판매자 입금
 */
@Slf4j
@Configuration
public class SettlementJobConfiguration {

	/**
	 * 정산 배치 Job
	 * Step 1: settlementStep - Settlement 생성
	 * Step 2: depositStep - 판매자에게 입금
	 */
	@Bean
	public Job settlementJob(JobRepository jobRepository, Step settlementStep, Step depositStep) {

		log.info("정산 Job 생성 - 2단계 Step 체인");

		return new JobBuilder("settlementJob_" + UUID.randomUUID(), jobRepository)
			.start(settlementStep)
			.next(depositStep)
			.build();
	}
}
