package io.devground.dbay.common.saga.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.common.saga.entity.Saga;
import io.devground.dbay.common.saga.repository.SagaRepository;
import io.devground.dbay.common.saga.vo.SagaStatus;
import io.devground.dbay.common.saga.vo.SagaStep;
import io.devground.dbay.common.saga.vo.SagaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SagaService {

	private final SagaRepository sagaRepository;

	public String startSaga(String referenceCode, SagaType sagaType) {

		return sagaRepository.findFirstByReferenceCodeAndSagaTypeAndSagaStatusOrderByStartedAtDesc(
				referenceCode, sagaType, SagaStatus.IN_PROCESS
			).map(Saga::getSagaId)
			.orElseGet(() -> {
				Saga saga = Saga.builder()
					.sagaType(sagaType)
					.referenceCode(referenceCode)
					.sagaStatus(SagaStatus.IN_PROCESS)
					.currentStep(SagaStep.INIT)
					.build();

				return sagaRepository.save(saga).getSagaId();
			});
	}

	public void updateStep(String sagaId, SagaStep step) {

		Saga saga = getSaga(sagaId);

		if (saga.getSagaStatus().isTerminal()) {
			log.warn("이미 종료된 Saga/업데이트 중지 - SagaId: {}, Status: {}, Step: {}", sagaId, saga.getSagaStatus(), step);

			return;
		}

		if (saga.getSagaStatus().isCompensating()) {
			log.warn("보상 중인 Saga/업데이트 중지 - SagaId: {}, Status: {}, Step: {}", sagaId, saga.getSagaStatus(), step);

			return;
		}

		if (saga.getCurrentStep() == step) {
			return;
		}

		saga.updateStep(step);
	}

	public void updateToSuccess(String sagaId) {

		Saga saga = getSaga(sagaId);

		if (saga.getSagaStatus().isTerminal()) {
			log.warn("이미 종료된 Saga/성공 처리 중지 - SagaId: {}, Status: {}", sagaId, saga.getSagaStatus());

			return;
		}

		saga.updateStep(SagaStep.COMPLETE);
		saga.updateStatus(SagaStatus.SUCCESS);
	}

	public void updateToFail(String sagaId, String errorMsg) {

		Saga saga = getSaga(sagaId);

		if (saga.getSagaStatus().isTerminal()) {
			log.warn("이미 종료된 Saga/실패 처리 중지 - SagaId: {}, Status: {}", sagaId, saga.getSagaStatus());

			return;
		}

		saga.updateToFail(errorMsg);
	}

	public void updateToCompensating(String sagaId) {

		Saga saga = getSaga(sagaId);

		if (saga.getSagaStatus().isTerminal()) {
			log.warn("이미 종료된 Saga/보상 처리 중지 - SagaId: {}, Status: {}", sagaId, saga.getSagaStatus());

			return;
		}

		saga.updateStep(SagaStep.COMPENSATING);
		saga.updateStatus(SagaStatus.COMPENSATING);
	}

	public void updateToCompensated(String sagaId, String message) {

		Saga saga = getSaga(sagaId);

		if (!saga.getSagaStatus().isCompensating()) {
			log.warn("보상 중이 아닌 Saga/보상 완료 처리 중지 - SagaId: {}, Status: {}", sagaId, saga.getSagaStatus());

			return;
		}

		saga.updateToCompensated(message);
	}

	@Transactional(readOnly = true)
	public Saga findLatestSagaByReferenceCode(String referenceCode) {

		return sagaRepository.findFirstByReferenceCodeAndSagaStatusOrderByStartedAtDesc(
				referenceCode,
				SagaStatus.IN_PROCESS
			)
			.orElseThrow(ErrorCode.SAGA_NOT_FOUND::throwServiceException);
	}

	@Transactional(readOnly = true)
	public Saga getSaga(String sagaId) {

		return sagaRepository.findBySagaId(sagaId)
			.orElseThrow(ErrorCode.SAGA_NOT_FOUND::throwServiceException);
	}
}
