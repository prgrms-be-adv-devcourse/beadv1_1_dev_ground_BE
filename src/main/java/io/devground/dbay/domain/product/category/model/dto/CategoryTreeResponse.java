package io.devground.dbay.domain.product.category.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record CategoryTreeResponse(

	long id,

	@NonNull
	String name,

	int depth,

	boolean isLeaf,

	@NonNull
	List<CategoryTreeResponse> children
) {
}
