package io.devground.dbay.domain.product.product.model.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record GetAllProductsResponse(

	@NonNull
	String productCode,

	@NonNull
	String title,

	String thumbnailUrl,

	long price
) {
}
