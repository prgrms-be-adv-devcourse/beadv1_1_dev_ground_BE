package io.devground.core.dto.image;

import io.devground.core.model.vo.ImageType;
import lombok.Builder;

// TODO: 사용하지 않을 시 삭제
@Builder
public record UploadImagesRequest(

	String referenceCode,
	
	ImageType imageType
) {
}
