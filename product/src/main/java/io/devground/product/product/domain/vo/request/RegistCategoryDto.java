package io.devground.product.product.domain.vo.request;

import io.devground.product.product.domain.vo.ProductDomainErrorCode;

public record RegistCategoryDto(

	String name,
	Long parentId
) {
	public RegistCategoryDto {
		if (name == null || name.isBlank()) {
			ProductDomainErrorCode.CATEGORY_MUST_BE_INPUT.throwException();
		}
	}
}