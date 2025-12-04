package io.devground.product.infrastructure.mapper;

import io.devground.product.domain.model.Category;
import io.devground.product.infrastructure.model.persistence.CategoryEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryMapper {

	public Category toDomain(CategoryEntity categoryEntity) {

		CategoryEntity parent = categoryEntity.getParent();

		Category category = new Category(parent.getCode(), parent.getDepth(), categoryEntity.getName(),
			parent.getFullPath());

		category.updateId(categoryEntity.getId());

		return category;
	}
}
