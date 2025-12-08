package io.devground.product.image.domain.vo;

import io.devground.core.model.vo.ImageType;

public record ImageSpec(

	String referenceCode,
	ImageType imageType,
	String imageUrl
) {
	public ImageSpec {
		if (referenceCode == null || referenceCode.isBlank()) {
			ImageDomainErrorCode.REFERENCE_CODE_MUST_BE_INPUT.throwException();
		}

		if (imageType == null) {
			ImageDomainErrorCode.IMAGE_TYPE_MUST_BE_INPUT.throwException();
		}

		if (imageUrl == null || imageUrl.isBlank()) {
			ImageDomainErrorCode.IMAGE_URL_MUST_BE_INPUT.throwException();
		}
	}
}
