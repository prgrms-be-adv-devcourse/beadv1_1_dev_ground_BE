package io.devground.dbay.domain.product.category.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.product.category.dto.AdminCategoryResponse;
import io.devground.dbay.domain.product.category.dto.CategoryResponse;
import io.devground.dbay.domain.product.category.dto.CategoryTreeResponse;
import io.devground.dbay.domain.product.category.dto.RegistCategoryRequest;
import io.devground.dbay.domain.product.category.entity.Category;
import io.devground.dbay.domain.product.category.mapper.CategoryMapper;
import io.devground.dbay.domain.product.category.repository.CategoryRepository;
import io.devground.dbay.domain.product.category.service.CategoryService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	// TODO: 관리자 인가 확인
	@Override
	public AdminCategoryResponse registCategory(RegistCategoryRequest request) {
		Category parent = null;
		int depth = 1;

		if (request.parentId() != null) {
			parent = categoryRepository.findById(request.parentId())
				.orElseThrow(ErrorCode.CATEGORY_NOT_FOUND::throwServiceException);
			depth = parent.getDepth() + 1;
		}

		Category category = Category.builder()
			.name(request.name())
			.depth(depth)
			.parent(parent)
			.build();

		if (parent != null) {
			parent.addChildren(category);
		}

		categoryRepository.save(category);

		return CategoryMapper.adminResponseFromCategoryAndParent(category, parent);
	}

	// TODO: 관리자 인가 확인
	@Override
	@Transactional(readOnly = true)
	public List<CategoryTreeResponse> getCategoryTree() {
		List<Category> rootCategories = categoryRepository.findCategoriesByParentIsNullOrderByNameAsc();

		return CategoryMapper.treeResponsesFromCategories(rootCategories);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> getRootCategories() {
		List<Category> rootCategories = categoryRepository.findCategoriesByParentIsNullOrderByNameAsc();

		return CategoryMapper.responsesFromCategories(rootCategories);
	}

	@Override
	public List<CategoryResponse> getChildCategories(Long parentId) {
		if (!categoryRepository.existsCategoryById(parentId)) {
			ErrorCode errorCode = ErrorCode.CATEGORY_NOT_FOUND;
		}

		List<Category> childCategories = categoryRepository.findCategoriesByParentIdOrderByNameAsc(parentId);

		return CategoryMapper.responsesFromCategories(childCategories);
	}
}
