package io.devground.product.product.infrastructure.adapter.out.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.product.product.infrastructure.model.persistence.ProductSaleEntity;

public interface ProductSaleJpaRepository extends JpaRepository<ProductSaleEntity, Long> {

	Optional<ProductSaleEntity> findByCode(String code);
}
