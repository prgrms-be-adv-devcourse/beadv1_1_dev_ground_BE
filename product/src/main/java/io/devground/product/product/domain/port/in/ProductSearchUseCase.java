package io.devground.product.product.domain.port.in;

import io.devground.product.product.domain.vo.pagination.PageDto;
import io.devground.product.product.domain.vo.request.ProductSearchDto;
import io.devground.product.product.domain.vo.request.ProductSuggestDto;
import io.devground.product.product.domain.vo.response.ProductSearchResponse;
import io.devground.product.product.domain.vo.response.ProductSuggestResponse;

public interface ProductSearchUseCase {

	PageDto<ProductSearchResponse> searchProducts(ProductSearchDto request);

	ProductSuggestResponse suggestCompletion(ProductSuggestDto request);

	ProductSuggestResponse suggestRelated(ProductSuggestDto request);
}
