package io.devground.dbay.domain.product.category.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record CategoryTreeResponse(
	long id,
	String name,
	int depth,
	boolean isLeaf,
	List<CategoryTreeResponse> children
) {
}
