package io.devground.product.domain.vo.response;

import io.devground.product.infrastructure.model.persistence.CategoryEntity;

public record CategoryResponse(
	long id,
	String name,
	boolean isLeaf
) {
	public CategoryResponse(CategoryEntity category) {
		this(
			category.getId(),
			category.getName(),
			category.isLeaf()
		);
	}
}
