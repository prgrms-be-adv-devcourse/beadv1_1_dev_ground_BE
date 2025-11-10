package io.devground.dbay.domain.product.category.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record AdminCategoryResponse(

	long id,

	@NonNull
	String name,

	int depth,

	Long parentId,

	boolean isLeaf
) {
}
