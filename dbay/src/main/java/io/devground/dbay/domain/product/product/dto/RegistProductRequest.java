package io.devground.dbay.domain.product.product.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistProductRequest(
	@NotBlank(message = "상품 제목은 필수입니다.")
	@Size(min = 1, max = 100, message = "상품 제목은 1자 이상 100자 이하로 입력해주세요.")
	String title,

	@NotBlank(message = "상품 설명은 필수입니다.")
	@Size(min = 10, max = 2000, message = "상품 설명은 10자 이상 2000자 이하로 입력해주세요.")
	String description,

	@NotNull(message = "카테고리는 필수입니다.")
	Long categoryId,

	@NotNull(message = "판매 가격은 필수입니다.")
	@Min(value = 0, message = "판매 가격은 0원 이상이어야 합니다.")
	Long price,

	@NotNull(message = "이미지 URL 목록은 필수입니다.")
	@Size(min = 1, max = 10, message = "이미지는 1개 이상 10개 이하로 등록 가능합니다.")
	List<String> imageUrls
) {
}
