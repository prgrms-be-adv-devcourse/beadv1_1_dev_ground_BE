package io.devground.dbay.domain.product.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.product.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
