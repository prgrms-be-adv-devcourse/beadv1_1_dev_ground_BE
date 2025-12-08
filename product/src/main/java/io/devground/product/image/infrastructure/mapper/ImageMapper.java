package io.devground.product.image.infrastructure.mapper;

import io.devground.core.model.vo.ImageType;
import io.devground.product.image.domain.model.Image;
import io.devground.product.image.domain.vo.ImageSpec;
import io.devground.product.image.infrastructure.model.persistence.ImageEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageMapper {

	public ImageEntity of(ImageType imageType, String referenceCode, String imageUrls) {

		return ImageEntity.builder()
			.imageType(imageType)
			.referenceCode(referenceCode)
			.imageUrl(imageUrls)
			.build();
	}

	public Image toImageDomain(ImageEntity imageEntity) {

		ImageSpec imageSpec = new ImageSpec(
			imageEntity.getReferenceCode(),
			imageEntity.getImageType(),
			imageEntity.getImageUrl()
		);

		return new Image(imageSpec);
	}

	public ImageEntity toImageEntity(Image image) {

		ImageSpec imageSpec = image.getImageSpec();

		return of(imageSpec.imageType(), imageSpec.referenceCode(), imageSpec.imageUrl());
	}
}
