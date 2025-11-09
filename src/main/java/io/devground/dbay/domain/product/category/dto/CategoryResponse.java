package io.devground.dbay.domain.product.category.dto;

import lombok.Builder;

@Builder
public record CategoryResponse(
	Long id,
	String name,
	boolean isLeaf
) {
}
