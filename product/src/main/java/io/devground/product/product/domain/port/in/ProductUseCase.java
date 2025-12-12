package io.devground.product.product.domain.port.in;

import java.util.List;

import io.devground.product.product.domain.vo.request.CartProductsDto;
import io.devground.product.product.domain.vo.request.ProductImageUrlsDto;
import io.devground.product.product.domain.vo.request.RegistProductDto;
import io.devground.product.product.domain.vo.request.UpdateProductDto;
import io.devground.product.product.domain.vo.pagination.PageDto;
import io.devground.product.product.domain.vo.pagination.PageQuery;
import io.devground.product.product.domain.vo.response.CartProductsResponse;
import io.devground.product.product.domain.vo.response.GetAllProductsResponse;
import io.devground.product.product.domain.vo.response.ProductDetailResponse;
import io.devground.product.product.domain.vo.response.RegistProductResponse;
import io.devground.product.product.domain.vo.response.UpdateProductResponse;

public interface ProductUseCase {

	PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest);

	RegistProductResponse registProduct(String sellerCode, RegistProductDto request);

	Void saveImageUrls(String sellerCode, String productCode, ProductImageUrlsDto request);

	ProductDetailResponse getProductDetail(String userCode, String productCode);

	UpdateProductResponse updateProduct(String sellerCode, String productCode, UpdateProductDto request);

	Void deleteProduct(String sellerCode, String productCode);

	List<CartProductsResponse> getCartProducts(CartProductsDto request);

	Void updateStatusToSold(String sellerCode, CartProductsDto request);

	void updateThumbnail(String productCode, String thumbnail);
}
