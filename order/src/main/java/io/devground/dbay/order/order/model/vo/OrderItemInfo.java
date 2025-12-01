package io.devground.dbay.order.order.model.vo;

public record OrderItemInfo(
	String code,
	String productName,
	Long productPrice
) {
}
