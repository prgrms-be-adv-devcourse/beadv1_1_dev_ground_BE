package io.devground.dbay.domain.product.product.dto;

import lombok.NonNull;

public record CartProductsResponse(

	@NonNull
	String productCode,

	@NonNull
	String productSaleCode,

	@NonNull
	String title,

	long price
) {
}
