package io.devground.dbay.domain.cart.cart.model.vo;

import lombok.NonNull;

public record CartProductListResponse(
	String productCode,

	String productSaleCode,

	String sellerCode,

	String title,

	Long price
) {
}
