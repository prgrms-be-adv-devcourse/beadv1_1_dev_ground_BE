package io.devground.product.application.port.out.persistence;

import java.util.List;

import io.devground.product.domain.vo.response.CategoryResponse;

public interface CategoryPersistencePort {

	List<CategoryResponse> findRootCategories();

	List<CategoryResponse> findChildCategories(Long parentId);
}
