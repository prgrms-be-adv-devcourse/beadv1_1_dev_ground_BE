package io.devground.product.infrastructure.adapter.out;

import org.springframework.stereotype.Repository;

import io.devground.product.application.port.out.persistence.ProductPersistencePort;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {

	private final ProductJpaRepository productRepository;
	private final ProductSaleJpaRepository productSaleRepository;
}
