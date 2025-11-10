package io.devground.dbay.domain.product.product.service;

import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductRequest;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;

public interface ProductService {

	RegistProductResponse registProduct(String sellerCode, RegistProductRequest request);

	UpdateProductResponse updateProduct(String sellerCode, String productCode, UpdateProductRequest request);
}
