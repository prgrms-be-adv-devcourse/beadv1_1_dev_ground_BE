package io.devground.dbay.domain.product.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.product.product.model.entity.ProductSale;

public interface ProductSaleRepository extends JpaRepository<ProductSale, Long> {

	Optional<ProductSale> findByCode(String code);
}
