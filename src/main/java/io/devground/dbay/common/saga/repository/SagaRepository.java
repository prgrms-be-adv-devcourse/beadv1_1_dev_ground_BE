package io.devground.dbay.common.saga.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.devground.dbay.common.saga.entity.Saga;
import io.devground.dbay.common.saga.vo.SagaStatus;
import io.devground.dbay.common.saga.vo.SagaStep;
import io.devground.dbay.common.saga.vo.SagaType;

public interface SagaRepository extends JpaRepository<Saga, Long> {

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

	@Query("""
		SELECT s
		FROM Saga s
		WHERE s.currentStep in :steps
		AND s.startedAt < :startedAt
		AND s.sagaStatus = 'IN_PROCESS'
		""")
	List<Saga> findSagasByCurrentStepInAndStartedAtBefore(
		@Param("steps") List<SagaStep> steps,
		@Param("startedAt") LocalDateTime startedAt
	);

	List<Saga> findAllBySagaStatusInAndUpdatedAtBefore(List<SagaStatus> sagaStatus, LocalDateTime updatedAt);
}
