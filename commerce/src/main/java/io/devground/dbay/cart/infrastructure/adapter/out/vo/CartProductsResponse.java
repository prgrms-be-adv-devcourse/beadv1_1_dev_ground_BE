package io.devground.dbay.cart.infrastructure.adapter.out.vo;

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
