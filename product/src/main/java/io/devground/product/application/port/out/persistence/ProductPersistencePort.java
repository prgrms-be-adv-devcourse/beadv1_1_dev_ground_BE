package io.devground.product.application.port.out.persistence;

import java.util.List;

import io.devground.product.application.model.RegistProductDto;
import io.devground.product.application.model.UpdateProductSoldDto;
import io.devground.product.domain.model.Product;
import io.devground.product.domain.vo.pagination.PageDto;
import io.devground.product.domain.vo.pagination.PageQuery;
import io.devground.product.domain.vo.response.GetAllProductsResponse;

public interface ProductPersistencePort {

	PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest);

	Product getProductByCode(String code);

	Product save(String sellerCode, RegistProductDto request);

	void updateThumbnail(String productCode, String thumbnail);

	List<Product> getProductsByCodes(String sellerCode, List<String> productCodes);

	void updateToSold(List<UpdateProductSoldDto> updatedProductsInfo);
}
