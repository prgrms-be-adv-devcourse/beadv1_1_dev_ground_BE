package io.devground.product.product.infrastructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import io.devground.product.product.domain.model.Category;
import io.devground.product.product.domain.vo.response.AdminCategoryResponse;
import io.devground.product.product.domain.vo.response.CategoryTreeResponse;
import io.devground.product.product.infrastructure.model.persistence.CategoryEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryMapper {

	public Category toDomain(CategoryEntity categoryEntity) {

		if (categoryEntity == null) {
			return null;
		}

		Category parent = null;
		if (categoryEntity.getParent() != null) {
			parent = toDomainWithoutChildren(categoryEntity.getParent());
		}

		Category category = Category.from(
			categoryEntity.getId(),
			categoryEntity.getCode(),
			categoryEntity.getCreatedAt(),
			categoryEntity.getUpdatedAt(),
			parent,
			categoryEntity.getName(),
			categoryEntity.getDepth(),
			categoryEntity.getFullPath()
		);

		List<Category> children = categoryEntity.getChildren().stream()
			.map(CategoryMapper::toDomain)
			.collect(Collectors.toList());

		category.linkChildren(children);

		return category;
	}

	public AdminCategoryResponse toAdminResponse(Category category) {

		Long parentId = category.getParent() != null ? category.getParent().getId() : null;

		return new AdminCategoryResponse(
			category.getId(), category.getName(), category.getDepth(), parentId, category.isLeaf()
		);
	}

	public CategoryTreeResponse toTreeResponse(Category category) {
		List<CategoryTreeResponse> children = category.getChildren().stream()
			.map(CategoryMapper::toTreeResponse)
			.toList();

		return new CategoryTreeResponse(
			category.getId(), category.getName(), category.getDepth(), category.isLeaf(), children
		);
	}

	public List<CategoryTreeResponse> toTreeResponses(List<Category> categories) {
		return categories.stream()
			.map(CategoryMapper::toTreeResponse)
			.toList();
	}

	private Category toDomainWithoutChildren(CategoryEntity categoryEntity) {

		if (categoryEntity == null) {
			return null;
		}

		Category parent = null;
		if (categoryEntity.getParent() != null) {
			parent = toDomainWithoutChildren(categoryEntity.getParent());
		}

		return Category.from(
			categoryEntity.getId(),
			categoryEntity.getCode(),
			categoryEntity.getCreatedAt(),
			categoryEntity.getUpdatedAt(),
			parent,
			categoryEntity.getName(),
			categoryEntity.getDepth(),
			categoryEntity.getFullPath()
		);
	}
}
