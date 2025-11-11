package io.devground.core.event.product;

import java.util.List;

import io.devground.core.model.vo.ImageType;
import lombok.NonNull;

public record ProductImagePushEvent(

	@NonNull
	ImageType imageType,

	@NonNull
	String referenceCode,

	@NonNull
	List<String> imageUrls
) {
}
