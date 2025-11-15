package io.devground.core.event.image;

import io.devground.core.event.vo.EventType;
import io.devground.core.model.vo.ImageType;
import lombok.NonNull;

public record ImageProcessedEvent(

	String sagaId,

	@NonNull
	ImageType imageType,

	@NonNull
	String referenceCode,

	@NonNull
	EventType eventType,

	String thumbnailUrl,

	boolean isSuccess,

	String errorMsg
) {
}
