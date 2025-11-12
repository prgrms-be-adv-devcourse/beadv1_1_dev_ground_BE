package io.devground.dbay.domain.saga.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.saga.entity.Saga;

public interface SagaRepository extends JpaRepository<Saga, Long> {

	Optional<Saga> findBySagaId(String sagaId);

	Optional<Saga> findTopByReferenceCodeAndSagaTypeOrderByStartedAtDesc(String referenceCode, String sagaType);
}
