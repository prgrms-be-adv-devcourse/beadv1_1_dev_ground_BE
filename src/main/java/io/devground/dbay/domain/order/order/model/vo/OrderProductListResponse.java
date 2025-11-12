package io.devground.dbay.domain.order.order.model.vo;

import lombok.NonNull;

public record OrderProductListResponse(
	String productCode,

	String productSaleCode,

	String sellerCode,

	String title,

	long price
) {
}
