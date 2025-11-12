package io.devground.dbay.domain.saga.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.saga.entity.Saga;
import io.devground.dbay.domain.saga.repository.SagaRepository;
import io.devground.dbay.domain.saga.vo.SagaStatus;
import io.devground.dbay.domain.saga.vo.SagaStep;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SagaService {

	private final SagaRepository sagaRepository;

	public String startSaga(String referenceCode, String sagaType) {

		Saga saga = Saga.builder()
			.sagaType(sagaType)
			.referenceCode(referenceCode)
			.sagaStatus(SagaStatus.IN_PROCESS)
			.currentStep(SagaStep.INIT)
			.build();

		return sagaRepository.save(saga).getSagaId();
	}

	public void updatedStep(String sagaId, SagaStep step) {

		Saga saga = getSaga(sagaId);

		saga.updateStep(step);
		saga.updateStatus(SagaStatus.IN_PROCESS);
	}

	public void updateToSuccess(String sagaId) {

		Saga saga = getSaga(sagaId);

		saga.updateStep(SagaStep.COMPLETE);
		saga.updateStatus(SagaStatus.SUCCESS);
	}

	public void updateToFail(String sagaId) {

		Saga saga = getSaga(sagaId);

		saga.updateStep(SagaStep.COMPENSATING);
		saga.updateStatus(SagaStatus.COMPENSATING);
	}

	@Transactional(readOnly = true)
	private Saga getSaga(String sagaId) {

		return sagaRepository.findBySagaId(sagaId)
			.orElseThrow(ErrorCode.SAGA_NOT_FOUND::throwServiceException);
	}
}
