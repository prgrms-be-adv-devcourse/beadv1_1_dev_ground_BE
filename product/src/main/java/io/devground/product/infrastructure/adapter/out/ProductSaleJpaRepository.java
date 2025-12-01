package io.devground.product.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.product.infrastructure.model.persistence.ProductSaleEntity;

public interface ProductSaleJpaRepository extends JpaRepository<ProductSaleEntity, Long> {
}
