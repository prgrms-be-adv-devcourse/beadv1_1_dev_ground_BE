package io.devground.dbay.domain.product.product.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PresignedUrlRequest(
	@NotEmpty(message = "파일 확장자는 필수입니다.")
	@Size(min = 1, max = 10, message = "파일은 1개 이상 10개 이하로 업로드 가능합니다.")
	List<String> fileExtensions
) {
}
