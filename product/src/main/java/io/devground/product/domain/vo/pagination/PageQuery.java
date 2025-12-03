package io.devground.product.domain.vo.pagination;

import io.devground.product.domain.vo.DomainErrorCode;

public record PageQuery(
	int page,
	int size,
	SortSpec sort
) {
	public PageQuery(int page, int size, SortSpec sort) {
		if (page <= 0) {
			DomainErrorCode.PAGE_MUST_BE_POSITIVE.throwException();
		}

		if (size <= 0) {
			DomainErrorCode.PAGE_SIZE_MUST_BE_POSITIVE.throwException();
		}

		this.page = page - 1;
		this.size = size;
		this.sort = sort;
	}
}
