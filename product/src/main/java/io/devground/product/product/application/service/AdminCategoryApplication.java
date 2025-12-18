package io.devground.product.product.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.product.product.application.port.out.persistence.CategoryPersistencePort;
import io.devground.product.product.domain.model.Category;
import io.devground.product.product.domain.port.in.AdminCategoryUseCase;
import io.devground.product.product.domain.vo.request.RegistCategoryDto;
import io.devground.product.product.domain.vo.response.AdminCategoryResponse;
import io.devground.product.product.domain.vo.response.CategoryTreeResponse;
import io.devground.product.product.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCategoryApplication implements AdminCategoryUseCase {

	private final CategoryPersistencePort categoryPort;

	// TODO: 관리자 인가 필요
	@Override
	public AdminCategoryResponse registCategory(RegistCategoryDto request) {

		Category parent = null;
		if (request.parentId() != null) {
			parent = categoryPort.getCategory(request.parentId());
		}

		Category category = Category.of(parent, request.name());

		if (parent != null) {
			parent.addChild(category);
		}

		Category savedCategory = categoryPort.save(category);

		return CategoryMapper.toAdminResponse(savedCategory);
	}

	// TODO: 관리자 인가 필요
	@Override
	@Transactional(readOnly = true)
	public List<CategoryTreeResponse> getCategoryTree() {

		List<Category> rootCategories = categoryPort.findRootCategories();

		return CategoryMapper.toTreeResponses(rootCategories);
	}
}
