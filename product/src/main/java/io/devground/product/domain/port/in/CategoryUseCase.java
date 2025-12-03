package io.devground.product.domain.port.in;

import java.util.List;

import io.devground.product.domain.vo.response.CategoryResponse;

public interface CategoryUseCase {

	List<CategoryResponse> getRootCategories();

	List<CategoryResponse> getChildCategories(Long parentId);
}
