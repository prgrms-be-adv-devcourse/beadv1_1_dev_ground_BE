package io.devground.core.event.image;

import io.devground.core.model.vo.ImageType;
import lombok.NonNull;

public record ImageDeleteEvent(

	@NonNull
	ImageType imageType,

	@NonNull
	String referenceCode
) {
}
