package io.devground.dbay.domain.product.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.product.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findCategoriesByParentIsNullOrderByNameAsc();
}
