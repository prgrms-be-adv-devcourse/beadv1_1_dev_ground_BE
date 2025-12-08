package io.devground.product.product.application.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.product.product.application.port.out.ProductSearchPort;
import io.devground.product.product.domain.port.in.ProductSearchUseCase;
import io.devground.product.product.domain.vo.SuggestOption;
import io.devground.product.product.domain.vo.SuggestType;
import io.devground.product.product.domain.vo.pagination.PageDto;
import io.devground.product.product.domain.vo.request.ProductSearchDto;
import io.devground.product.product.domain.vo.request.ProductSuggestDto;
import io.devground.product.product.domain.vo.response.ProductSearchResponse;
import io.devground.product.product.domain.vo.response.ProductSearchResult;
import io.devground.product.product.domain.vo.response.ProductSuggestResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductSearchApplication implements ProductSearchUseCase {

	private final ProductSearchPort searchPort;

	@Override
	public PageDto<ProductSearchResponse> searchProducts(ProductSearchDto request) {

		ProductSearchResult results = searchPort.searchProducts(request);

		return new PageDto<>(
			results.currentPageNumber(),
			results.pageSize(),
			results.totalPages(),
			results.totalItems(),
			results.items()
		);
	}

	@Override
	public ProductSuggestResponse suggestCompletion(ProductSuggestDto request) {

		SuggestType suggestType = SuggestType.COMPLETION;

		if (request.keyword() == null || request.keyword().isBlank()) {
			return new ProductSuggestResponse(request.keyword(), suggestType, Collections.emptyList());
		}

		List<SuggestOption> suggestions = searchPort.completeSuggest(request);

		return new ProductSuggestResponse(request.keyword(), suggestType, suggestions);
	}

	@Override
	public ProductSuggestResponse suggestRelated(ProductSuggestDto request) {

		SuggestType suggestType = SuggestType.RELATED;

		if (request.keyword() == null || request.keyword().isBlank()) {
			return new ProductSuggestResponse(request.keyword(), suggestType, Collections.emptyList());
		}

		List<SuggestOption> suggestions = searchPort.relatedSuggest(request);

		return new ProductSuggestResponse(request.keyword(), suggestType, suggestions);
	}
}
