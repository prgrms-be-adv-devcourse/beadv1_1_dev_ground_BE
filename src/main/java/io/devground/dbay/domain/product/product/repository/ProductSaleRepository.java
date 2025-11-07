package io.devground.dbay.domain.product.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.product.product.entity.ProductSale;

public interface ProductSaleRepository extends JpaRepository<ProductSale, Long> {
}
