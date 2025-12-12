package io.devground.product.product.application.port.out.persistence;

import java.util.List;

import io.devground.product.product.domain.vo.request.CartProductsDto;
import io.devground.product.product.domain.vo.request.RegistProductDto;
import io.devground.product.product.domain.vo.request.UpdateProductSoldDto;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.model.ProductSale;
import io.devground.product.product.domain.vo.pagination.PageDto;
import io.devground.product.product.domain.vo.pagination.PageQuery;
import io.devground.product.product.domain.vo.response.CartProductsResponse;
import io.devground.product.product.domain.vo.response.GetAllProductsResponse;

public interface ProductPersistencePort {

	PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest);

	Product getProductByCode(String code);

	Product save(String sellerCode, RegistProductDto request);

	void updateThumbnail(String productCode, String thumbnail);

	List<Product> getProductsByCodes(String sellerCode, List<String> productCodes);

	void updateToSold(String sellerCode, UpdateProductSoldDto updatedProductsSoldDto);

	void updateProduct(String sellerCode, Product product, ProductSale productSale);

	void deleteProduct(String sellerCode, Product product);

	List<CartProductsResponse> getCartProducts(CartProductsDto request);

	List<Product> getProductsByCodes(List<String> productCodes);
}
