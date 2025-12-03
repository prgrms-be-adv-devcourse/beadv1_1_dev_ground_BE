package io.devground.product.infrastructure.mapper;

import io.devground.product.domain.model.Category;
import io.devground.product.infrastructure.model.persistence.CategoryEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryMapper {

	public Category toDomain(CategoryEntity category) {

		CategoryEntity parent = category.getParent();

		return new Category(
			parent.getCode(),
			parent.getDepth(),
			category.getName(),
			parent.getFullPath()
		);
	}
}
