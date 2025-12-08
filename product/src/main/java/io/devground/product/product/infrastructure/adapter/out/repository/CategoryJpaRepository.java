package io.devground.product.product.infrastructure.adapter.out.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.devground.product.product.infrastructure.model.persistence.CategoryEntity;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {

	@Query("""
		SELECT c
		FROM CategoryEntity c
		JOIN FETCH c.children
		WHERE c.parent IS NULL
		ORDER BY c.name ASC
		""")
	List<CategoryEntity> findRootCategories();

	List<CategoryEntity> findCategoriesByParentIdOrderByNameAsc(Long parentId);

	boolean existsCategoryById(Long id);
}
