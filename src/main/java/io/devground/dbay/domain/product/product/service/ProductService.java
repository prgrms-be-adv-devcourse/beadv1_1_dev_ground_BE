package io.devground.dbay.domain.product.product.service;

import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;

public interface ProductService {

	RegistProductResponse registProduct(String sellerCode, RegistProductRequest request);
}
