package io.devground.product.application.port.out.persistence;

import io.devground.product.domain.model.Product;
import io.devground.product.domain.vo.pagination.PageDto;
import io.devground.product.domain.vo.pagination.PageQuery;
import io.devground.product.domain.vo.response.GetAllProductsResponse;
import io.devground.product.infrastructure.model.web.request.RegistProductRequest;

public interface ProductPersistencePort {

	PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest);

	Product getProductByCode(String code);

	Product save(String sellerCode, RegistProductRequest request);

	void updateThumbnail(String productCode, String thumbnail);
}
