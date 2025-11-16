package io.devground.dbay.domain.product.category.service;

import java.util.List;

import io.devground.dbay.domain.product.category.model.dto.AdminCategoryResponse;
import io.devground.dbay.domain.product.category.model.dto.CategoryResponse;
import io.devground.dbay.domain.product.category.model.dto.CategoryTreeResponse;
import io.devground.dbay.domain.product.category.model.dto.RegistCategoryRequest;

public interface CategoryService {
	AdminCategoryResponse registCategory(RegistCategoryRequest request);

	List<CategoryTreeResponse> getCategoryTree();

	List<CategoryResponse> getRootCategories();

	List<CategoryResponse> getChildCategories(Long parentId);
}
