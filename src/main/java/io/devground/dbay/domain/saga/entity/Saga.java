package io.devground.dbay.domain.saga.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import io.devground.dbay.domain.saga.vo.SagaStatus;
import io.devground.dbay.domain.saga.vo.SagaStep;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Saga {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, updatable = false)
	private String sagaId;

	@Column(nullable = false)
	private String sagaType;

	@Column(nullable = false)
	private String referenceCode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SagaStatus sagaStatus;

	@Enumerated(EnumType.STRING)
	private SagaStep currentStep;

	private String lastErrorMessage;

	private LocalDateTime startedAt;

	private LocalDateTime updatedAt;

	@Version
	private Long version;

	@Builder
	public Saga(String sagaType, String referenceCode, SagaStatus sagaStatus, SagaStep currentStep) {
		this.sagaType = sagaType;
		this.referenceCode = referenceCode;
		this.sagaStatus = sagaStatus;
		this.currentStep = currentStep;
	}

	@PrePersist
	void init() {
		if (this.sagaId == null) {
			this.sagaId = UUID.randomUUID().toString();
		}

		this.startedAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	void updateTime() {
		this.updatedAt = LocalDateTime.now();
	}

	public void updateStep(SagaStep step) {
		this.currentStep = step;
	}

	public void updateStatus(SagaStatus sagaStatus) {
		this.sagaStatus = sagaStatus;
	}

	public void updateToFail(String message) {
		this.sagaStatus = SagaStatus.FAILED;
		this.lastErrorMessage = message;
	}
}
