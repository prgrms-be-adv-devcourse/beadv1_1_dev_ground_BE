package io.devground.dbay.domain.cart.cart.model.vo;

import lombok.NonNull;

public record CartProductListResponse(
	@NonNull
	String productCode,

	@NonNull
	String productSaleCode,

	@NonNull
	String title,

	long price
) {
}
