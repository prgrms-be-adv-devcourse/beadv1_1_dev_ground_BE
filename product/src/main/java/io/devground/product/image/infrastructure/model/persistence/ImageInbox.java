package io.devground.product.image.infrastructure.model.persistence;

import io.devground.core.event.vo.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "imageInbox",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_saga_image_inbox",
		columnNames = {"sagaId", "eventType"}
	)
)
public class ImageInbox {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String sagaId;

	@Enumerated
	@Column(nullable = false)
	private EventType eventType;

	@Column(nullable = false)
	private String referenceCode;

	@Builder
	public ImageInbox(String sagaId, EventType eventType, String referenceCode) {
		this.sagaId = sagaId;
		this.eventType = eventType;
		this.referenceCode = referenceCode;
	}
}
