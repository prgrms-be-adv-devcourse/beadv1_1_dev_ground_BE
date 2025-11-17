package io.devground.core.dto.image;

import java.util.List;

import io.devground.core.model.vo.ImageType;
import lombok.Builder;

@Builder
public record DeleteImagesRequest(

	ImageType imageType,

	String referenceCode,

	List<String> deleteUrls
) {
}
