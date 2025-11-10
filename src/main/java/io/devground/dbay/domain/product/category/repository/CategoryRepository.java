package io.devground.dbay.domain.product.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.devground.dbay.domain.product.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("""
		SELECT c
		FROM Category c
		JOIN FETCH c.children
		WHERE c.parent IS NULL
		ORDER BY c.name ASC
		""")
	List<Category> findCategoriesByParentIsNullOrderByNameAsc();
}
