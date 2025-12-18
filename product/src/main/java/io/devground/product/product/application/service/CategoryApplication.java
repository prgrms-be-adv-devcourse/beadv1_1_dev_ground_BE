package io.devground.product.product.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.product.product.application.port.out.persistence.CategoryPersistencePort;
import io.devground.product.product.domain.model.Category;
import io.devground.product.product.domain.port.in.CategoryUseCase;
import io.devground.product.product.domain.vo.response.CategoryResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryApplication implements CategoryUseCase {

	private final CategoryPersistencePort categoryPort;

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> getRootCategories() {

		List<Category> categories = categoryPort.findRootCategories();

		return categories.stream()
			.map(category -> new CategoryResponse(category.getId(), category.getName(), category.isLeaf()))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> getChildCategories(Long parentId) {

		List<Category> categories = categoryPort.findChildCategories(parentId);

		return categories.stream()
			.map(category -> new CategoryResponse(category.getId(), category.getName(), category.isLeaf()))
			.toList();
	}
}
