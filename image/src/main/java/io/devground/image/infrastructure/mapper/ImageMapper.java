package io.devground.image.infrastructure.mapper;

import io.devground.core.model.vo.ImageType;
import io.devground.image.infrastructure.model.persistence.ImageEntity;
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
}
