package io.devground.product.infrastructure.model.web.request;

import java.util.List;

import org.springframework.util.StringUtils;

import io.devground.core.model.vo.ErrorCode;
import io.devground.core.model.vo.ImageType;

public record ImageUploadPlan(

	ImageType imageType,
	String referenceCode,
	List<String> fileExtensions
) {
	public ImageUploadPlan {
		if (imageType == null) {
			ErrorCode.IMAGE_TYPE_MUST_BE_INPUT.throwServiceException();
		}

		if (!StringUtils.hasText(referenceCode)) {
			ErrorCode.CODE_MUST_BE_INPUT.throwServiceException();
		}
	}
}

