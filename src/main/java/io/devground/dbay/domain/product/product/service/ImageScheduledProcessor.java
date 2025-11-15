package io.devground.dbay.domain.product.product.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ImageType;
import io.devground.dbay.common.saga.entity.Saga;
import io.devground.dbay.common.saga.repository.SagaRepository;
import io.devground.dbay.common.saga.service.SagaService;
import io.devground.dbay.common.saga.vo.SagaStatus;
import io.devground.dbay.common.saga.vo.SagaStep;
import io.devground.dbay.domain.product.product.client.ImageClient;
import io.devground.dbay.domain.product.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO: 실행 Term 전략 세우기
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageScheduledProcessor {

	private final SagaRepository sagaRepository;
	private final SagaService sagaService;
	private final ImageClient imageClient;

	@Scheduled(fixedDelay = 300000)
	@Transactional
	public void checkTimeoutSagas() {

		LocalDateTime deadline = LocalDateTime.now().minusMinutes(10);

		List<Saga> timeoutSagas =
			sagaRepository.findSagaByCurrentStepAndStartedAtBefore(SagaStep.WAITING_S3_UPLOAD, deadline);

		if (timeoutSagas.isEmpty()) {
			return;
		}

		for (Saga timeoutSaga : timeoutSagas) {
			try {
				this.compensateTimeoutSaga(timeoutSaga);
			} catch (Exception e) {
				log.error("Timeout된 Saga 처리 실패 - SagaId: {}, ProductCode: {}, Exception: {}",
					timeoutSaga.getSagaId(), timeoutSaga.getReferenceCode(), e.getMessage());
			}
		}
	}

	@Scheduled(fixedDelay = 600000)
	@Transactional
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

	private void compensateTimeoutSaga(Saga saga) {

		String sagaId = saga.getSagaId();
		String productCode = saga.getReferenceCode();

		try {
			sagaService.updateToCompensating(sagaId);

			imageClient.compensateUpload(ProductMapper.toDeleteImagesRequest(ImageType.PRODUCT, productCode))
				.throwIfNotSuccess();

			sagaService.updateToCompensated(sagaId, "Timeout된 Saga 보상 처리 완료");

			log.info("Timeout된 Saga 보상 처리 완료 - SagaId: {}, ProductCode: {}", sagaId, productCode);
		} catch (Exception e) {
			log.error("Timeout된 Saga 보상 처리 실패 - SagaId: {}, ProductCode: {}, Exception: {}",
				sagaId, productCode, e.getMessage()
			);

			sagaService.updateToFail(
				sagaId,
				String.format("Timeout된 Saga 보상 실패/수동 처리 필요: ProductCode: {}, Exception: {}",
					productCode, e.getMessage())
			);

			throw e;
		}
	}
}
