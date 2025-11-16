package io.devground.dbay.domain.product.product.model.dto;

import lombok.NonNull;

public record CartProductsResponse(

	@NonNull
	String productCode,

	@NonNull
	String productSaleCode,

	@NonNull
	String sellerCode,

	@NonNull
	String title,

	long price
) {
}
