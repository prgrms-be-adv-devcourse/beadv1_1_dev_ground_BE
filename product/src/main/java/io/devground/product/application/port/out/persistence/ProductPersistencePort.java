package io.devground.product.application.port.out.persistence;

import io.devground.product.domain.vo.pagination.PageDto;
import io.devground.product.domain.vo.pagination.PageQuery;
import io.devground.product.domain.vo.response.GetAllProductsResponse;

public interface ProductPersistencePort {

	PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest);
}
