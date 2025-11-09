package io.devground.dbay.domain.product.category.service;

import io.devground.dbay.domain.product.category.dto.AdminCategoryResponse;
import io.devground.dbay.domain.product.category.dto.RegistCategoryRequest;

public interface CategoryService {
	AdminCategoryResponse registCategory(RegistCategoryRequest request);
}
