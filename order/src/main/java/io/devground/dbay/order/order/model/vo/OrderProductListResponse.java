package io.devground.dbay.order.order.model.vo;

public record OrderProductListResponse(
	String productCode,

	String productSaleCode,

	String sellerCode,

	String title,

	long price
) {
}
