package io.devground.dbay.domain.product.category.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistCategoryRequest(

	@NotBlank(message = "카테고리 이름은 필수입니다.")
	@Size(min = 1, max = 50, message = "카테고리 이름은 50자까지만 가능합니다.")
	String name,

	Long parentId
) {
}
