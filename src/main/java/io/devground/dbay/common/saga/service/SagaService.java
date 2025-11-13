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

@Service
@Transactional
@RequiredArgsConstructor
public class SagaService {

	private final SagaRepository sagaRepository;

	public String startSaga(String referenceCode, SagaType sagaType) {

		Saga saga = Saga.builder()
			.sagaType(sagaType)
			.referenceCode(referenceCode)
			.sagaStatus(SagaStatus.IN_PROCESS)
			.currentStep(SagaStep.INIT)
			.build();

		return sagaRepository.save(saga).getSagaId();
	}

	public void updateStep(String sagaId, SagaStep step) {

		Saga saga = getSaga(sagaId);

		saga.updateStep(step);
	}

	public void updateToSuccess(String sagaId) {

		Saga saga = getSaga(sagaId);

		saga.updateStep(SagaStep.COMPLETE);
		saga.updateStatus(SagaStatus.SUCCESS);
	}

	public void updateToFail(String sagaId, String errorMsg) {

		Saga saga = getSaga(sagaId);

		saga.updateToFail(errorMsg);
	}

	public void updateToCompensating(String sagaId) {

		Saga saga = getSaga(sagaId);

		saga.updateStep(SagaStep.COMPENSATING);
		saga.updateStatus(SagaStatus.COMPENSATING);
	}

	// Step 조건 없이 해당 Code의 최신 Saga 조회
	@Transactional(readOnly = true)
	public Saga findLatestSagaByReferenceCode(String referenceCode) {

		return sagaRepository.findFirstByReferenceCodeAndSagaStatusOrderByStartedAtDesc(
				referenceCode,
				SagaStatus.IN_PROCESS
			)
			.orElseThrow(ErrorCode.SAGA_NOT_FOUND::throwServiceException);
	}

	@Transactional(readOnly = true)
	public void isExistSagaById(String sagaId) {

		if (!sagaRepository.existsBySagaId(sagaId)) {
			ErrorCode.SAGA_NOT_FOUND.throwServiceException();
		}
	}

	@Transactional(readOnly = true)
	protected Saga getSaga(String sagaId) {

		return sagaRepository.findBySagaId(sagaId)
			.orElseThrow(ErrorCode.SAGA_NOT_FOUND::throwServiceException);
	}
}
