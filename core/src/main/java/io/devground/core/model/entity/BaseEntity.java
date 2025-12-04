package io.devground.core.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.core.util.CodeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"code", "createdAt"})
public abstract class BaseEntity {

	@Column(nullable = false, unique = true)
	private String code;

	@Enumerated(EnumType.STRING)
	private DeleteStatus deleteStatus = DeleteStatus.N;

	@CreatedDate
	private LocalDateTime createdAt = LocalDateTime.now();

	@LastModifiedDate
	private LocalDateTime updatedAt = LocalDateTime.now();

	@PrePersist
	protected void onCreate() {
		if (this.createdAt == null)
			this.createdAt = LocalDateTime.now();

		if (this.updatedAt == null)
			this.updatedAt = LocalDateTime.now();
	}

	public void register(String code) {
		this.code = code;
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
}
