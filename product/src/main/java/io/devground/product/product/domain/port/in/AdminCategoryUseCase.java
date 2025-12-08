package io.devground.product.product.domain.port.in;

import java.util.List;

import io.devground.product.product.domain.vo.request.RegistCategoryDto;
import io.devground.product.product.domain.vo.response.AdminCategoryResponse;
import io.devground.product.product.domain.vo.response.CategoryTreeResponse;

public interface AdminCategoryUseCase {

	AdminCategoryResponse registCategory(RegistCategoryDto request);

	List<CategoryTreeResponse> getCategoryTree();
}
