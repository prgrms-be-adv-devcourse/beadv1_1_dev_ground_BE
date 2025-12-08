package io.devground.product.image.infrastructure.model.web.request;

import java.util.List;

import io.devground.core.model.vo.ImageType;
import jakarta.validation.constraints.NotNull;

public record DeleteImagesRequest(

	@NotNull(message = "이미지 타입은 반드시 입력되어야 합니다.")
	ImageType imageType,

	@NotNull(message = "이미지 참조 코드는 반드시 입력되어야 합니다.")
	String referenceCode,

	List<String> deleteUrls
) {
}
