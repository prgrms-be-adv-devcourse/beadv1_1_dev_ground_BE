package io.devground.core.event.product;

import java.util.List;

import io.devground.core.model.vo.ImageType;
import lombok.NonNull;

public record ProductImagesDeleteEvent(

	String sagaId,

	@NonNull
	ImageType imageType,

	@NonNull
	String referenceCode,

	List<String> deleteUrls
) {
}
