package io.devground.product.product.infrastructure.adapter.out;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.devground.core.model.vo.ErrorCode;
import io.devground.product.product.application.port.out.persistence.CategoryPersistencePort;
import io.devground.product.product.domain.model.Category;
import io.devground.product.product.domain.vo.ProductDomainErrorCode;
import io.devground.product.product.infrastructure.adapter.out.repository.CategoryJpaRepository;
import io.devground.product.product.infrastructure.mapper.CategoryMapper;
import io.devground.product.product.infrastructure.model.persistence.CategoryEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryPersistencePort {

	private final CategoryJpaRepository categoryRepository;

	@Override
	public List<Category> findRootCategories() {

		List<CategoryEntity> rootCategories = categoryRepository.findRootCategories();

		return rootCategories.stream()
			.map(CategoryMapper::toDomain)
			.toList();
	}

	@Override
	public List<Category> findChildCategories(Long parentId) {

		if (!categoryRepository.existsCategoryById(parentId)) {
			ErrorCode.CATEGORY_NOT_FOUND.throwServiceException();
		}

		List<CategoryEntity> childCategories = categoryRepository.findCategoriesByParentIdOrderByNameAsc(parentId);

		return childCategories.stream()
			.map(CategoryMapper::toDomain)
			.toList();
	}

	@Override
	public Category getCategory(Long categoryId) {

		CategoryEntity categoryEntity = this.getCategoryById(categoryId);

		return CategoryMapper.toDomain(categoryEntity);
	}

	@Override
	public Category save(Category category) {

		CategoryEntity parentEntity = null;
		if (category.getParent() != null) {
			parentEntity = categoryRepository.findById(category.getParent().getId())
				.orElseThrow(ProductDomainErrorCode.CATEGORY_NOT_FOUND::throwException);
		}

		CategoryEntity categoryEntity = CategoryEntity.builder()
			.code(category.getCode())
			.parent(parentEntity)
			.name(category.getName())
			.depth(category.getDepth())
			.build();

		if (parentEntity != null) {
			parentEntity.addChildren(categoryEntity);
		}

		CategoryEntity savedEntity = categoryRepository.save(categoryEntity);

		return CategoryMapper.toDomain(savedEntity);
	}

	private CategoryEntity getCategoryById(Long categoryId) {

		return categoryRepository.findById(categoryId)
			.orElseThrow(ProductDomainErrorCode.CATEGORY_NOT_FOUND::throwException);
	}
}
