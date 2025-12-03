package io.devground.product.application.port.out.persistence;

import java.util.Optional;

import io.devground.product.domain.model.Product;
import io.devground.product.domain.vo.pagination.PageDto;
import io.devground.product.domain.vo.pagination.PageQuery;
import io.devground.product.domain.vo.response.GetAllProductsResponse;

public interface ProductPersistencePort {

	PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest);

	Optional<Product> getProductByCode(String code);
}
