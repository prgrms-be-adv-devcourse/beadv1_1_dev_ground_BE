package io.devground.dbay.domain.order.order.model.vo;

public record UnsettledOrderItemInfo(
	String orderItemCode,
	String sellerCode,
	Long productPrice
) {
}
