package io.devground.dbay.domain.product.product.dto;

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
