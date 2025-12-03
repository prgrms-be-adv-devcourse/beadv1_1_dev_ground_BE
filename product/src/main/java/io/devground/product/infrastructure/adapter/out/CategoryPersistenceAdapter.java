package io.devground.product.infrastructure.adapter.out;

import org.springframework.stereotype.Repository;

import io.devground.product.application.port.out.persistence.CategoryPersistencePort;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryPersistencePort {

	private final CategoryJpaRepository categoryRepository;


}
