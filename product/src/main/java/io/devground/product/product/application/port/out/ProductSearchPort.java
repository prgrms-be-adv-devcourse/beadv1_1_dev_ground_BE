package io.devground.product.product.application.port.out;

import java.util.List;

import io.devground.product.product.domain.vo.SuggestOption;
import io.devground.product.product.domain.vo.request.ProductSearchDto;
import io.devground.product.product.domain.vo.request.ProductSuggestDto;
import io.devground.product.product.domain.vo.response.ProductSearchResult;

public interface ProductSearchPort {

	ProductSearchResult searchProducts(ProductSearchDto request);

	List<SuggestOption> completeSuggest(ProductSuggestDto request);

	List<SuggestOption> relatedSuggest(ProductSuggestDto request);
}
