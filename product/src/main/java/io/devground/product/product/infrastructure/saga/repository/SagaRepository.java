package io.devground.product.product.infrastructure.saga.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.devground.product.product.infrastructure.saga.entity.Saga;
import io.devground.product.product.infrastructure.saga.vo.SagaStatus;
import io.devground.product.product.infrastructure.saga.vo.SagaStep;
import io.devground.product.product.infrastructure.saga.vo.SagaType;

public interface SagaRepository extends JpaRepository<Saga, Long> {

	Optional<Saga> findBySagaId(String sagaId);

	Optional<Saga> findTopByReferenceCodeOrderByStartedAtDesc(String referenceCode);

	@Query("""
		SELECT s
		FROM Saga s
		WHERE s.referenceCode = :referenceCode
		AND s.sagaType = :sagaType
		AND s.sagaStatus IN :statuses
		ORDER BY s.startedAt DESC
		""")
	Optional<Saga> findInProgressByReferenceCodeAndType(
		@Param("referenceCode") String referenceCode,
		@Param("sagaType") SagaType sagaType,
		@Param("statuses") List<SagaStatus> statuses
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
