package io.devground.dbay.domain.product.category.service;

import java.util.List;

import io.devground.dbay.domain.product.category.dto.AdminCategoryResponse;
import io.devground.dbay.domain.product.category.dto.CategoryResponse;
import io.devground.dbay.domain.product.category.dto.CategoryTreeResponse;
import io.devground.dbay.domain.product.category.dto.RegistCategoryRequest;

public interface CategoryService {
	AdminCategoryResponse registCategory(RegistCategoryRequest request);

	List<CategoryTreeResponse> getCategoryTree();

	List<CategoryResponse> getRootCategories();
}
