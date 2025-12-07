package io.devground.dbay.order.domain.vo.pagination;


import io.devground.dbay.order.domain.exception.DomainError;

public record PageQuery(
	int page,
	int size,
	SortSpec sort
) {
	public PageQuery(int page, int size, SortSpec sort) {
		if (page <= 0) {
			DomainError.PAGE_MUST_BE_POSITIVE.throwDomainException();
		}

		if (size <= 0) {
			DomainError.PAGE_SIZE_MUST_BE_POSITIVE.throwDomainException();
		}

		this.page = page - 1;
		this.size = size;
		this.sort = sort;
	}
}
