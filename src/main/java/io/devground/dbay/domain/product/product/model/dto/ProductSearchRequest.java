package io.devground.dbay.domain.product.product.model.dto;

import java.util.List;

public record ProductSearchRequest(

	String keyword,

	List<Long> categoryIds,

	Long minPrice,

	Long maxPrice,

	String sellerCode,

	String productStatus,

	String sortBy,

	String sortDirection,

	Integer page,

	Integer size
) {
	public ProductSearchRequest {
		if (sortBy == null) {
			sortBy = "createdAt";
		}

		if (sortDirection == null) {
			sortDirection = "desc";
		}

		if (page == null || page < 1) {
			page = 1;
		}

		if (size == null || size <= 0 || size > 100) {
			size = 10;
		}
	}
}
