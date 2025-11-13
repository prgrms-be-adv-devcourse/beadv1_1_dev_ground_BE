package io.devground.dbay.domain.order.order.model.vo;

public record OrderItemInfo(
	String code,
	String productName,
	Long productPrice
) {
}
