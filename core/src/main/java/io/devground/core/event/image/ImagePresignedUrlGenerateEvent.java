package io.devground.core.event.image;

import java.net.URL;
import java.util.List;

import io.devground.core.model.vo.ImageType;
import lombok.NonNull;

public record ImagePresignedUrlGenerateEvent(

	@NonNull
	ImageType imageType,

	@NonNull
	String referenceCode,

	List<URL> presignedUrls
) {
}
