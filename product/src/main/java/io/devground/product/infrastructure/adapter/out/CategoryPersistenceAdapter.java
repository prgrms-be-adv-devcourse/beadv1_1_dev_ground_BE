package io.devground.product.infrastructure.adapter.out;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.devground.core.model.vo.ErrorCode;
import io.devground.product.application.port.out.persistence.CategoryPersistencePort;
import io.devground.product.domain.model.Category;
import io.devground.product.domain.vo.DomainErrorCode;
import io.devground.product.domain.vo.response.CategoryResponse;
import io.devground.product.infrastructure.mapper.CategoryMapper;
import io.devground.product.infrastructure.model.persistence.CategoryEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryPersistencePort {

	private final CategoryJpaRepository categoryRepository;

	@Override
	public List<CategoryResponse> findRootCategories() {

		List<CategoryEntity> rootCategories = categoryRepository.findCategoriesByParentIsNullOrderByNameAsc();

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
			.orElseThrow(DomainErrorCode.CATEGORY_NOT_FOUND::throwException);

		return CategoryMapper.toDomain(categoryEntity);
	}
}
