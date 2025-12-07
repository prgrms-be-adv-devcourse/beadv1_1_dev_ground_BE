package io.devground.image.domain.vo;

import io.devground.core.model.vo.ImageType;

public record ImageSpec(

	String referenceCode,
	ImageType imageType,
	String imageUrl
) {
	public ImageSpec {
		if (referenceCode == null || referenceCode.isBlank()) {
			DomainErrorCode.REFERENCE_CODE_MUST_BE_INPUT.throwException();
		}

		if (imageType == null) {
			DomainErrorCode.IMAGE_TYPE_MUST_BE_INPUT.throwException();
		}

		if (imageUrl == null || imageUrl.isBlank()) {
			DomainErrorCode.IMAGE_URL_MUST_BE_INPUT.throwException();
		}
	}
}
