package io.devground.dbay.domain.product.category.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record CategoryResponse(

	long id,

	@NonNull
	String name,

	boolean isLeaf
) {
}
