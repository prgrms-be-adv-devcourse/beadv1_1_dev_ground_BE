package io.devground.product.product.domain.vo.response;

import java.util.List;

public record ProductSearchResult(

	int currentPageNumber,
	int pageSize,
	long totalPages,
	long totalItems,
	List<ProductSearchResponse> items
) {
}
