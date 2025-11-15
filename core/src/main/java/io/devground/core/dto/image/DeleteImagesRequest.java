package io.devground.core.dto.image;

import io.devground.core.model.vo.ImageType;
import lombok.Builder;

@Builder
public record DeleteImagesRequest(

	ImageType imageType,

	String referenceCode
) {
}
