package io.devground.core.event.image;

import java.util.List;

import io.devground.core.model.vo.ImageType;
import lombok.NonNull;

public record ImagePushEvent(

	@NonNull
	ImageType imageType,

	@NonNull
	String referenceCode,

	@NonNull
	List<String> imageUrls
) {
}
