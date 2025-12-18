package io.devground.core.model.web;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.NonNull;

public record PageDto<T>(
	int currentPageNumber,
	int pageSize,
	long totalPages,
	long totalItems,

	@NonNull
	List<T> items
) {
	public static <T> PageDto<T> from(Page<T> page) {
		return new PageDto<>(
			page.getNumber() + 1,
			page.getSize(),
			page.getTotalPages(),
			page.getTotalElements(),
			page.getContent()
		);
	}
}
