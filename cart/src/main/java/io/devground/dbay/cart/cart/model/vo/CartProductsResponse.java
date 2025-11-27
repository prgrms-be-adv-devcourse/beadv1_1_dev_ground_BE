package io.devground.dbay.cart.cart.model.vo;

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
