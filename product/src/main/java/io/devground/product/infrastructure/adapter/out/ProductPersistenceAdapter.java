package io.devground.product.infrastructure.adapter.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.devground.product.application.port.out.persistence.ProductPersistencePort;
import io.devground.product.domain.vo.pagination.PageDto;
import io.devground.product.domain.vo.pagination.PageQuery;
import io.devground.product.domain.vo.response.GetAllProductsResponse;
import io.devground.product.infrastructure.mapper.PageMapper;
import io.devground.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.infrastructure.util.PageUtils;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {

	private final ProductJpaRepository productRepository;
	private final ProductSaleJpaRepository productSaleRepository;

	@Override
	public PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest) {

		Pageable pageable = PageUtils.convertToSafePageable(pageRequest);

		Page<ProductEntity> products = productRepository.findAllWithSale(pageable);

		Page<GetAllProductsResponse> responses = products
			.map(product -> new GetAllProductsResponse(product, product.getProductSale()));

		return PageMapper.from(responses);
	}
}
