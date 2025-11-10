package io.devground.dbay.domain.product.product.service;

import java.util.List;

import io.devground.dbay.domain.product.product.dto.CartProductsRequest;
import io.devground.dbay.domain.product.product.dto.CartProductsResponse;
import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductRequest;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;

public interface ProductService {

	RegistProductResponse registProduct(String sellerCode, RegistProductRequest request);

	UpdateProductResponse updateProduct(String sellerCode, String productCode, UpdateProductRequest request);

	List<CartProductsResponse> getCartProducts(CartProductsRequest request);

	void deleteProduct(String productCode);

	void updateStatusToSold(CartProductsRequest request);
}
