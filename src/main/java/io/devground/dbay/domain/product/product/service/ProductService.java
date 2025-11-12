package io.devground.dbay.domain.product.product.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.devground.dbay.domain.product.product.dto.CartProductsRequest;
import io.devground.dbay.domain.product.product.dto.CartProductsResponse;
import io.devground.dbay.domain.product.product.dto.ProductDetailResponse;
import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductRequest;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;

public interface ProductService {

	RegistProductResponse registProduct(String sellerCode, RegistProductRequest request, MultipartFile[] files);

	ProductDetailResponse getProductDetail(String productCode);

	UpdateProductResponse updateProduct(
		String sellerCode, String productCode, MultipartFile[] files, UpdateProductRequest request
	);

	List<CartProductsResponse> getCartProducts(CartProductsRequest request);

	Void deleteProduct(String productCode);

	void updateStatusToSold(CartProductsRequest request);
}
