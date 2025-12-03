package io.devground.product.infrastructure.adapter.out;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.devground.product.application.port.out.persistence.CategoryPersistencePort;
import io.devground.product.domain.vo.response.CategoryResponse;
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
}
