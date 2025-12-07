package io.devground.image.infrastructure.model;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ImageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
	name = "image",
	indexes = {
		@Index(name = "idx_image_type_reference_code_url", columnList = "imageType, referenceCode, imageUrl")
	},
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_image_reference_code_image_type_url", columnNames = {"referenceCode", "imageType", "imageUrl"}
		)
	}
)
public class ImageEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String referenceCode;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ImageType imageType;

	@Column(nullable = false)
	private String imageUrl;

	@Builder
	public ImageEntity(String referenceCode, ImageType imageType, String imageUrl) {
		this.referenceCode = referenceCode;
		this.imageType = imageType;
		this.imageUrl = imageUrl;
	}
}
