package io.devground.dbay.common.saga.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.common.saga.entity.Saga;
import io.devground.dbay.common.saga.vo.SagaStatus;
import io.devground.dbay.common.saga.vo.SagaStep;
import io.devground.dbay.common.saga.vo.SagaType;

public interface SagaRepository extends JpaRepository<Saga, Long> {

	boolean existsBySagaId(String sagaId);

	Optional<Saga> findBySagaId(String sagaId);

	Optional<Saga> findFirstByReferenceCodeAndSagaStatusOrderByStartedAtDesc(
		String referenceCode,
		SagaStatus sagaStatus
	);

	Optional<Saga> findFirstByReferenceCodeAndSagaTypeAndSagaStatusOrderByStartedAtDesc(
		String referenceCode,
		SagaType sagaType,
		SagaStatus sagaStatus
	);

	List<Saga> findSagaByCurrentStepAndStartedAtBefore(SagaStep currentStep, LocalDateTime startedAt);

	List<Saga> findSagaBySagaStatusAndUpdatedAtBefore(SagaStatus sagaStatus, LocalDateTime updatedAt);
}
