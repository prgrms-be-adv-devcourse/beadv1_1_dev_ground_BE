package io.devground.dbay.domain.image.dto;

import io.devground.dbay.domain.image.vo.ImageType;
import lombok.NonNull;

public record ImagePushEvent(

	@NonNull
	ImageType imageType,

	@NonNull
	String referenceCode
) {
}
