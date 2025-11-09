package io.devground.dbay.domain.product.category.mapper;

import io.devground.dbay.domain.product.category.dto.CategoryResponse;
import io.devground.dbay.domain.product.category.dto.AdminCategoryResponse;
import io.devground.dbay.domain.product.category.entity.Category;

public abstract class CategoryMapper {

	public static CategoryResponse responseFromCategory(Category category) {
		return CategoryResponse.builder()
			.id(category.getId())
			.name(category.getName())
			.isLeaf(category.isLeaf())
			.build();
	}

	public static AdminCategoryResponse adminResponseFromCategoryAndParent(Category category, Category parent) {
		return AdminCategoryResponse.builder()
			.id(category.getId())
			.name(category.getName())
			.depth(category.getDepth())
			.isLeaf(category.isLeaf())
			.parentId(parent != null ? parent.getId() : null)
			.build();
	}
}
