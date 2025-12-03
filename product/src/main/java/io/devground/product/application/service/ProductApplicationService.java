package io.devground.product.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.product.application.port.out.persistence.CategoryPersistencePort;
import io.devground.product.application.port.out.persistence.ProductPersistencePort;
import io.devground.product.domain.port.in.CategoryUseCase;
import io.devground.product.domain.port.in.ProductUseCase;
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
import lombok.RequiredArgsConstructor;

// TODO: Dbay 제거 후 ProductApplication으로 롤백
@Service
@Transactional
@RequiredArgsConstructor
public class ProductApplicationService implements CategoryUseCase, ProductUseCase {

	private final CategoryPersistencePort categoryPort;
	private final ProductPersistencePort productPort;

	@Override
	public PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest) {

		throw new UnsupportedOperationException("구현 중");
	}

	@Override
	public RegistProductResponse registProduct(String sellerCode, RegistProductRequest request) {

		throw new UnsupportedOperationException("구현 중");
	}

	@Override
	public Void saveImageUrls(String sellerCode, String productCode, ProductImageUrlsRequest request) {

		throw new UnsupportedOperationException("구현 중");
	}

	@Override
	public ProductDetailResponse getProductDetail(String productCode) {

		throw new UnsupportedOperationException("구현 중");
	}

	@Override
	public UpdateProductResponse updateProduct(String sellerCode, String productCode, UpdateProductRequest request) {

		throw new UnsupportedOperationException("구현 중");
	}

	@Override
	public Void deleteProduct(String sellerCode, String productCode) {

		throw new UnsupportedOperationException("구현 중");
	}

	@Override
	public List<CartProductsResponse> getCartProducts(CartProductsRequest request) {

		throw new UnsupportedOperationException("구현 중");
	}

	@Override
	public void updateStatusToSold(String sellerCode, CartProductsRequest request) {

		throw new UnsupportedOperationException("구현 중");
	}
}
