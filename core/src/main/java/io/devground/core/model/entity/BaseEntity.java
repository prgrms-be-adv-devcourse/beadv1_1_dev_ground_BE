package io.devground.core.model.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.devground.core.model.vo.DeleteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@Column(nullable = false, unique = true)
	private String code = UUID.randomUUID().toString();

	@Enumerated(EnumType.STRING)
	private DeleteStatus deleteStatus = DeleteStatus.N;

	@CreatedDate
	private LocalDateTime createdAt = LocalDateTime.now();

	@LastModifiedDate
	private LocalDateTime updatedAt = LocalDateTime.now();

	@PrePersist
	protected void onCellCreated() {
		if (this.createdAt == null)
			this.createdAt = LocalDateTime.now();

		if (this.updatedAt == null)
			this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		if (this.updatedAt == null)
			this.updatedAt = LocalDateTime.now();
	}

	public void delete() {
		this.deleteStatus = DeleteStatus.Y;
		this.updatedAt = LocalDateTime.now();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		BaseEntity bo = (BaseEntity) o;
		return Objects.equals(code, bo.code) && Objects.equals(createdAt, bo.createdAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, createdAt);
	}
}
