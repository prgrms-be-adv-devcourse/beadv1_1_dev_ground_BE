package io.devground.core.model.vo;

import io.devground.core.model.exception.ServiceException;
import lombok.Getter;

@Getter
public enum ImageType {

	PRODUCT;

	public static ImageType fromName(String name) {
		for (ImageType imageType : ImageType.values()) {
			if (imageType.name().equalsIgnoreCase(name)) {
				return imageType;
			}
		}

		throw new ServiceException(ErrorCode.IMAGE_TYPE_CANNOT_CONVERT);
	}
}
