package io.devground.dbay.domain.product.product.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegistProductRequest(

	@NotNull(message = "카테고리는 필수로 선택해야 합니다.")
	Long categoryId,

	@NotBlank(message = "상품명은 필수입니다.")
	@Size(max = 100, message = "상품명은 100자까지만 가능합니다.")
	String title,

	@NotBlank(message = "상품 설명은 필수입니다.")
	@Size(max = 3000, message = "상품 설명은 3000자까지만 가능합니다.")
	String description,

	@NotNull(message = "가격은 필수입니다.")
	@Positive(message = "가격은 0원 이상이어야 합니다.")
	Long price,

	List<String> fileExtension
) {
}
