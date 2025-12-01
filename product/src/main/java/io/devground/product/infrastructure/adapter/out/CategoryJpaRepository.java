package io.devground.product.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.product.infrastructure.model.persistence.CategoryEntity;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
}
