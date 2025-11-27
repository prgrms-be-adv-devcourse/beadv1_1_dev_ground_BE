package io.devground.dbay.settlement.batch.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.KafkaException;
import org.springframework.transaction.PlatformTransactionManager;

import io.devground.core.commands.deposit.SettlementChargeDeposit;

import io.devground.dbay.settlement.batch.listener.SettlementStepListener;
import io.devground.dbay.settlement.batch.processor.SettleConvertProcessor;
import io.devground.dbay.settlement.batch.processor.SettlementDepositProcessor;
import io.devground.dbay.settlement.batch.reader.SettlementDepositReader;
import io.devground.dbay.settlement.batch.reader.UnsettledOrderItemReader;
import io.devground.dbay.settlement.batch.writer.SettlementDataWriter;
import io.devground.dbay.settlement.batch.writer.SettlementDepositWriter;
import io.devground.dbay.settlement.model.dto.UnsettledOrderItemResponse;
import io.devground.dbay.settlement.model.entity.Settlement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 정산 배치 Step 구성
 * Reader → Processor → Writer 흐름을 정의
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SettlementStepConfiguration {

	@Value("${custom.batch.chunk.size}")
	private Integer batchSize;

	@Value("${custom.batch.skip.limit}")
	private Integer skipLimit;

	@Value("${custom.batch.retry.limit}")
	private Integer retryLimit;

	/**
	 * 정산 처리 Step
	 * - 청크 크기: 100건씩 처리
	 * - Reader: Order 도메인에서 정산 대상 OrderItem 조회
	 * - Processor: OrderItem DTO를 Settlement 엔티티로 변환
	 * - Writer: Settlement 엔티티를 DB에 저장
	 * - Skip 정책: 특정 예외 발생 시 해당 항목 건너뜀
	 * - Retry 정책: 일시적 오류 발생 시 재시도
	 */
	@Bean
	public Step settlementStep(JobRepository jobRepository
		, PlatformTransactionManager ptManager
		, UnsettledOrderItemReader unsettledOrderItemReader
		, SettleConvertProcessor settleConvertProcessor
		, SettlementDataWriter settlementDataWriter
	) {

		log.info("정산 Step 생성 - skipLimit={}, retryLimit={}", skipLimit, retryLimit);

		return new StepBuilder("settlementStep", jobRepository)
			.<UnsettledOrderItemResponse, Settlement>chunk(batchSize, ptManager)
			.reader(unsettledOrderItemReader)
			.processor(settleConvertProcessor)
			.writer(settlementDataWriter)
			// Skip 정책: 데이터 오류는 건너뛰기
			.faultTolerant()
			.skip(IllegalArgumentException.class)
			.skip(NullPointerException.class)
			.skipLimit(skipLimit)
			// Retry 정책: 일시적 오류는 재시도
			.retry(org.springframework.dao.DataAccessException.class)
			.retry(KafkaException.class)
			.retryLimit(retryLimit)
			// Skip/Retry 리스너
			.listener(new SettlementStepListener())
			.build();
	}

	/**
	 * 입금 처리 Step
	 * - 청크 크기: 100건씩 처리
	 * - Reader: SETTLEMENT_CREATED 상태의 Settlement 조회
	 * - Processor: Settlement를 SettlementChargeDeposit 커맨드로 변환
	 * - Writer: Saga Orchestrator를 통해 정산 입금 Saga 시작
	 * - Retry 정책: Kafka 전송 실패 시 재시도
	 */
	@Bean
	public Step depositStep(JobRepository jobRepository
		, PlatformTransactionManager ptManager
		, SettlementDepositReader settlementDepositReader
		, SettlementDepositProcessor settlementDepositProcessor
		, SettlementDepositWriter settlementDepositWriter
	) {

		log.info("입금 Step 생성 - retryLimit={}", retryLimit);

		return new StepBuilder("depositStep", jobRepository)
			.<Settlement, SettlementChargeDeposit>chunk(batchSize, ptManager)
			.reader(settlementDepositReader)
			.processor(settlementDepositProcessor)
			.writer(settlementDepositWriter)
			// Kafka 전송 실패 시 재시도
			.faultTolerant()
			.retry(KafkaException.class)
			.retryLimit(retryLimit)
			// Settlement 상태를 FAILED로 변경하기 위한 리스너
			.listener(new SettlementStepListener())
			.build();
	}
}
