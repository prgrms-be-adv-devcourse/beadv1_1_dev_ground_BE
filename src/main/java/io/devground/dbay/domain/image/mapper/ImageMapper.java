package io.devground.dbay.domain.image.mapper;

import io.devground.core.model.vo.ImageType;
import io.devground.dbay.domain.image.entity.Image;

public abstract class ImageMapper {

	public static Image of(ImageType imageType, String referenceCode, String imageUrl) {

		return Image.builder()
			.imageType(imageType)
			.referenceCode(referenceCode)
			.imageUrl(imageUrl)
			.build();
	}
}
