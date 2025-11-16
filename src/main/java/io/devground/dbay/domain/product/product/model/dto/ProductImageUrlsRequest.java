package io.devground.dbay.domain.product.product.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record ProductImageUrlsRequest(

	@NotBlank(message = "이미지 URL은 필수입니다.")
	List<String> urls
) {
}
