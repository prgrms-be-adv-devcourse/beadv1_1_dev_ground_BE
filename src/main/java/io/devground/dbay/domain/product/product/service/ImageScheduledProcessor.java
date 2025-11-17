package io.devground.dbay.domain.product.product.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.devground.dbay.common.saga.entity.Saga;
import io.devground.dbay.common.saga.repository.SagaRepository;
import io.devground.dbay.common.saga.service.SagaService;
import io.devground.dbay.common.saga.vo.SagaStatus;
import io.devground.dbay.common.saga.vo.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO: 실행 Term 전략 세우기
@Slf4j(topic = "sagaTimeout")
@Component
@Transactional
@RequiredArgsConstructor
public class ImageScheduledProcessor {

	private final SagaService sagaService;
	private final SagaRepository sagaRepository;
	private final ProductImageCompensationService compensationService;

	/**
	 * Timeout된 Saga 보상 처리
	 * 1. S3 업로드 대기 중에서 멈춘 상태
	 * 2. Kafka 메시지가 발행되었지만 deadline까지 처리되지 않은 상태
	 * 3. 현재는 5분마다 실행, 추후 모니터링 후 변동 가능
	 */
	@Scheduled(fixedDelay = 300000)
	public void checkTimeoutSagas() {

		LocalDateTime deadline = LocalDateTime.now().minusMinutes(10);

		List<Saga> timeoutSagas = sagaRepository.findSagaByCurrentStepInAndStartedAtBefore(
			List.of(SagaStep.WAITING_S3_UPLOAD, SagaStep.IMAGE_KAFKA_PUBLISHED),
			deadline
		);

		if (timeoutSagas.isEmpty()) {
			return;
		}

		for (Saga timeoutSaga : timeoutSagas) {
			try {
				log.error("Timeout된 Saga 보상 시작 - SagaId: {}, ProductCode: {}, Step: {}, Exception: {}",
					timeoutSaga.getSagaId(), timeoutSaga.getReferenceCode(),
					timeoutSaga.getCurrentStep(), timeoutSaga.getLastErrorMessage()
				);

				compensationService.compensateProductImageUploadFailure(
					timeoutSaga.getSagaId(), timeoutSaga.getReferenceCode(), "Saga Timeout"
				);

				log.error("Timeout된 Saga 보상 완료 - SagaId: {}, ProductCode: {}, Exception: {}",
					timeoutSaga.getSagaId(), timeoutSaga.getReferenceCode(), timeoutSaga.getLastErrorMessage());
			} catch (Exception e) {
				log.error("Timeout된 Saga 보상 실패 - SagaId: {}, ProductCode: {}, Exception: {}",
					timeoutSaga.getSagaId(), timeoutSaga.getReferenceCode(), e.getMessage());
			}
		}
	}

	/**
	 * 보상 처리 중 체류된 Saga 정리
	 * 현재는 10분마다 실행
	 */
	@Scheduled(fixedDelay = 600000)
	public void deleteSagasStayedInCompensating() {

		LocalDateTime deadline = LocalDateTime.now().minusMinutes(30);

		List<Saga> stayedSagas =
			sagaRepository.findSagaBySagaStatusAndUpdatedAtBefore(SagaStatus.COMPENSATING, deadline);

		if (stayedSagas.isEmpty()) {
			return;
		}

		for (Saga saga : stayedSagas) {
			log.error("미보상 상태로 멈춘 Saga/수동 처리 필요 - SagaId: {}, ProductCode: {}, Exception: {}",
				saga.getSagaId(), saga.getReferenceCode(), saga.getLastErrorMessage());

			sagaService.updateToFail(saga.getSagaId(), "미보상 상태로 멈춘 Saga/수동 처리 필요: " + saga.getLastErrorMessage());
		}
	}
}
