package io.devground.dbay.domain.product.category.mapper;

import java.util.List;

import io.devground.dbay.domain.product.category.dto.AdminCategoryResponse;
import io.devground.dbay.domain.product.category.dto.CategoryResponse;
import io.devground.dbay.domain.product.category.dto.CategoryTreeResponse;
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

	public static CategoryTreeResponse treeResponseFromCategory(Category category) {
		List<CategoryTreeResponse> children = category.getChildren().stream()
			.map(CategoryMapper::treeResponseFromCategory)
			.toList();

		return CategoryTreeResponse.builder()
			.id(category.getId())
			.name(category.getName())
			.depth(category.getDepth())
			.isLeaf(category.isLeaf())
			.children(children)
			.build();
	}

	public static List<CategoryTreeResponse> treeResponsesFromCategories(List<Category> categories) {
		return categories.stream()
			.map(CategoryMapper::treeResponseFromCategory)
			.toList();
	}
}
