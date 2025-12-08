package io.devground.product.product.application.port.out.persistence;

import java.util.List;

import io.devground.product.product.domain.model.Category;

public interface CategoryPersistencePort {

	List<Category> findRootCategories();

	List<Category> findChildCategories(Long parentId);

	Category getCategory(Long categoryId);

	Category save(Category category);
}
