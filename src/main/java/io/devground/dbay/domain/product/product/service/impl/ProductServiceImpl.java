package io.devground.dbay.domain.product.product.service.impl;

import org.springframework.stereotype.Service;

import io.devground.dbay.domain.product.category.repository.CategoryRepository;
import io.devground.dbay.domain.product.product.repository.ProductRepository;
import io.devground.dbay.domain.product.product.repository.ProductSaleRepository;
import io.devground.dbay.domain.product.product.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ProductSaleRepository productSaleRepository;
	private final CategoryRepository categoryRepository;
}
