package io.devground.product.product.application.port.out.persistence;

import java.util.List;

import io.devground.product.product.domain.model.Category;
import io.devground.product.product.domain.vo.response.CategoryResponse;

public interface CategoryPersistencePort {

	List<CategoryResponse> findRootCategories();

	List<CategoryResponse> findChildCategories(Long parentId);

	Category getCategory(Long categoryId);
}
