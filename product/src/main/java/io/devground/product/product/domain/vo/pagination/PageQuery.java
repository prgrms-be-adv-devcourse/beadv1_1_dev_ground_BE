package io.devground.product.product.domain.vo.pagination;

import io.devground.product.product.domain.vo.ProductDomainErrorCode;

public record PageQuery(
	int page,
	int size,
	SortSpec sort
) {
	public PageQuery(int page, int size, SortSpec sort) {
		if (page <= 0) {
			ProductDomainErrorCode.PAGE_MUST_BE_POSITIVE.throwException();
		}

		if (size <= 0) {
			ProductDomainErrorCode.PAGE_SIZE_MUST_BE_POSITIVE.throwException();
		}

		this.page = page - 1;
		this.size = size;
		this.sort = sort;
	}
}
