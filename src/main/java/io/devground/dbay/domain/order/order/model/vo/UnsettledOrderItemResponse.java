package io.devground.dbay.domain.order.order.model.vo;

public record UnsettledOrderItemResponse(
	String orderCode,
	String userCode,
	String orderItemCode,
	String sellerCode,
	Long productPrice
) {
}
