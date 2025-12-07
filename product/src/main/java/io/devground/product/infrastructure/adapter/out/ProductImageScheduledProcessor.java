package io.devground.product.infrastructure.adapter.out;

import static io.devground.product.infrastructure.saga.vo.SagaStep.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.devground.product.infrastructure.saga.entity.Saga;
import io.devground.product.infrastructure.saga.repository.SagaRepository;
import io.devground.product.infrastructure.saga.service.SagaService;
import io.devground.product.infrastructure.saga.vo.SagaStatus;
import io.devground.product.infrastructure.saga.vo.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO: 실행 Term 전략 세우기

@Slf4j(topic = "sagaTimeout")
@Component
@Transactional
@RequiredArgsConstructor
public class ProductImageScheduledProcessor {

	private final SagaService sagaService;
	private final SagaRepository sagaRepository;

	/**
	 * Timeout된 Saga 보상 처리
	 * 1. Kafka 메시지가 발행되었지만 deadline까지 처리되지 않은 상태
	 * 2. 현재는 5분마다 실행, 추후 모니터링 후 변동 가능
	 */
	@Scheduled(fixedDelay = 300000)
	public void checkTimeoutSagas() {

		LocalDateTime deadline = LocalDateTime.now().minusMinutes(10);

		List<Saga> timeoutSagas = sagaRepository.findSagasByCurrentStepInAndStartedAtBefore(
			List.of(IMAGE_KAFKA_PUBLISHED, IMAGE_DB_SAVE),
			deadline
		);

		if (timeoutSagas.isEmpty()) {
			return;
		}

		for (Saga timeoutSaga : timeoutSagas) {
			String sagaId = timeoutSaga.getSagaId();
			String productCode = timeoutSaga.getReferenceCode();
			SagaStep step = timeoutSaga.getCurrentStep();
			String errorMsg = timeoutSaga.getLastErrorMessage();

			if (step.equals(IMAGE_KAFKA_PUBLISHED)) {

				log.error("Timeout된 Saga 실패 처리 - SagaId: {}, ProductCode: {}, Step: {}, Exception: {}",
					sagaId, productCode,
					step, errorMsg
				);

				sagaService.updateToFail(
					sagaId,
					String.format("Timeout된 Saga 실패 처리 - SagaId: %s, ProductCode: %s", sagaId, productCode)
				);

				log.error("Timeout된 Saga 실패 처리 완료 - SagaId: {}, ProductCode: {}, Exception: {}",
					sagaId, productCode, errorMsg);
			} else {

				log.error(
					"Timeout된 Saga 보상 처리 수동 필요/S3-DB 정합성 문제 발생 - SagaId: {}, ProductCode: {}, Step: {}, Exception: {}",
					sagaId, productCode, step, errorMsg
				);

				sagaService.updateToFail(
					sagaId,
					String.format("Timeout된 Saga 보상 처리 수동 필요/S3-DB 정합성 문제 발생 - SagaId: %s, ProductCode: %s",
						sagaId, productCode
					)
				);
			}
		}
	}

	/**
	 * 진행/보상 처리 중 체류된 Saga 정리
	 * DLT에서까지 실패해서 어중간하게 머물러있는 Saga 정리
	 * 현재는 10분마다 실행
	 * 30분이 지났으면 실패 처리
	 */
	@Scheduled(fixedDelay = 600000)
	public void deleteSagasStayedInCompensating() {

		LocalDateTime deadline = LocalDateTime.now().minusMinutes(30);

		List<Saga> stayedSagas = sagaRepository.findAllBySagaStatusInAndUpdatedAtBefore(
			List.of(SagaStatus.IN_PROCESS, SagaStatus.COMPENSATING), deadline);

		if (stayedSagas.isEmpty()) {
			return;
		}

		for (Saga saga : stayedSagas) {
			log.error("진행/미보상 상태로 멈춘 Saga/수동 처리 필요 - SagaId: {}, ProductCode: {}, Exception: {}",
				saga.getSagaId(), saga.getReferenceCode(), saga.getLastErrorMessage());

			sagaService.updateToFail(saga.getSagaId(), "진행/미보상 상태로 멈춘 Saga/수동 처리 필요: " + saga.getLastErrorMessage());
		}
	}
}
