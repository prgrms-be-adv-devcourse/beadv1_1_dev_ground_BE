package io.devground.product.domain.port.in;

import java.util.List;

import io.devground.product.domain.vo.pagination.PageDto;
import io.devground.product.domain.vo.pagination.PageQuery;
import io.devground.product.domain.vo.response.CartProductsResponse;
import io.devground.product.domain.vo.response.GetAllProductsResponse;
import io.devground.product.domain.vo.response.ProductDetailResponse;
import io.devground.product.domain.vo.response.RegistProductResponse;
import io.devground.product.domain.vo.response.UpdateProductResponse;
import io.devground.product.infrastructure.model.web.request.CartProductsRequest;
import io.devground.product.infrastructure.model.web.request.ProductImageUrlsRequest;
import io.devground.product.infrastructure.model.web.request.RegistProductRequest;
import io.devground.product.infrastructure.model.web.request.UpdateProductRequest;

public interface ProductUseCase {

	PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest);

	RegistProductResponse registProduct(String sellerCode, RegistProductRequest request);

	Void saveImageUrls(String sellerCode, String productCode, ProductImageUrlsRequest request);

	ProductDetailResponse getProductDetail(String productCode);

	UpdateProductResponse updateProduct(String sellerCode, String productCode, UpdateProductRequest request);

	Void deleteProduct(String sellerCode, String productCode);

	List<CartProductsResponse> getCartProducts(CartProductsRequest request);

	void updateStatusToSold(String sellerCode, CartProductsRequest request);
}
