package io.devground.product.infrastructure.model.web.request;

import java.util.List;

import io.devground.core.model.vo.ImageType;

public record DeleteImagesRequest(

	ImageType imageType,
	String referenceCode,
	List<String> deleteUrls
) {
}
