package io.devground.product.infrastructure.adapter.out;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.devground.product.infrastructure.model.persistence.CategoryEntity;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {

	@Query("""
		SELECT c
		FROM CategoryEntity c
		JOIN FETCH c.children
		WHERE c.parent IS NULL
		ORDER BY c.name ASC
		""")
	List<CategoryEntity> findCategoriesByParentIsNullOrderByNameAsc();

	List<CategoryEntity> findCategoriesByParentIdOrderByNameAsc(Long parentId);

	boolean existsCategoryById(Long id);
}
