package io.devground.product.product.infrastructure.adapter.out;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.devground.core.model.vo.ErrorCode;
import io.devground.product.product.application.port.out.persistence.CategoryPersistencePort;
import io.devground.product.product.domain.model.Category;
import io.devground.product.product.domain.vo.ProductDomainErrorCode;
import io.devground.product.product.domain.vo.response.CategoryResponse;
import io.devground.product.product.infrastructure.adapter.out.repository.CategoryJpaRepository;
import io.devground.product.product.infrastructure.mapper.CategoryMapper;
import io.devground.product.product.infrastructure.model.persistence.CategoryEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryPersistencePort {

	private final CategoryJpaRepository categoryRepository;

	@Override
	public List<CategoryResponse> findRootCategories() {

		List<CategoryEntity> rootCategories = categoryRepository.findRootCategories();

		return rootCategories.stream()
			.map(CategoryResponse::new)
			.toList();
	}

	@Override
	public List<CategoryResponse> findChildCategories(Long parentId) {

		if (!categoryRepository.existsCategoryById(parentId)) {
			ErrorCode.CATEGORY_NOT_FOUND.throwServiceException();
		}

		List<CategoryEntity> childCategories = categoryRepository.findCategoriesByParentIdOrderByNameAsc(parentId);

		return childCategories.stream()
			.map(CategoryResponse::new)
			.toList();
	}

	@Override
	public Category getCategory(Long categoryId) {

		CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
			.orElseThrow(ProductDomainErrorCode.CATEGORY_NOT_FOUND::throwException);

		return CategoryMapper.toDomain(categoryEntity);
	}
}
