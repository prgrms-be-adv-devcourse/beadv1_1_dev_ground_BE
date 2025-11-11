package io.devground.dbay.domain.product.product.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record ProductDetailResponse(

	@NonNull
	String productCode,

	@NonNull
	String productSaleCode,

	@NonNull
	String sellerCode,

	@NonNull
	String title,

	@NonNull
	String description,

	@NonNull
	String categoryPath,

	long price,

	@NonNull
	String productStatus

) {
}
