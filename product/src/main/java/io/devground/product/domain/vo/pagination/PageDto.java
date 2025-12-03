package io.devground.product.domain.vo.pagination;

import java.util.List;

public record PageDto<T>(
	int currentPageNumber,
	int pageSize,
	long totalPages,
	long totalItems,

	List<T> items
) {
	public PageDto(
		int currentPageNumber,
		int pageSize,
		long totalPages,
		long totalItems,
		List<T> items
	) {
		this.currentPageNumber = currentPageNumber + 1;
		this.pageSize = pageSize;
		this.totalPages = totalPages;
		this.totalItems = totalItems;
		this.items = items;
	}
}
