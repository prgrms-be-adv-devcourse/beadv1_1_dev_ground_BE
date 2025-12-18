package io.devground.product.product.domain.vo.pagination;

import java.util.List;

public record PageDto<T>(
	int currentPageNumber,
	int pageSize,
	long totalPages,
	long totalItems,

	List<T> items
) {
}
