package io.devground.payments.common.saga.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import io.devground.payments.common.saga.vo.SagaStatus;
import io.devground.payments.common.saga.vo.SagaStep;
import io.devground.payments.common.saga.vo.SagaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	indexes = {
		@Index(name = "idx_step_started_status", columnList = "currentStep, startedAt, sagaStatus"),
		@Index(name = "idx_status_updated", columnList = "sagaStatus, updatedAt"),
		@Index(name = "idx_reference_code_type_status", columnList = "referenceCode, sagaType, sagaStatus")
	},
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_saga_reference_code_type_status",
			columnNames = {"referenceCode", "sagaType", "sagaStatus"}
		)
	}
)
public class Saga {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// TODO: id 대신 sagaId를 꼭 써야하는지 확인
	@Column(unique = true, nullable = false, updatable = false)
	private String sagaId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SagaType sagaType;

	@Column(nullable = false)
	private String referenceCode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SagaStatus sagaStatus;

	@Enumerated(EnumType.STRING)
	@Column(length = 100)
	private SagaStep currentStep;

	@Column(length = 1000)
	private String lastErrorMessage;

	private LocalDateTime startedAt;

	private LocalDateTime updatedAt;

	@Version
	private Long version;

	@Builder
	public Saga(SagaType sagaType, String referenceCode, SagaStatus sagaStatus, SagaStep currentStep) {
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
		this.currentStep = SagaStep.FAILED;
		this.sagaStatus = SagaStatus.FAILED;
		this.lastErrorMessage = message;
	}

	public void updateToCompensated(String message) {
		this.sagaStatus = SagaStatus.COMPENSATED;
		this.currentStep = SagaStep.COMPENSATED;
		this.lastErrorMessage = message;
	}
}
