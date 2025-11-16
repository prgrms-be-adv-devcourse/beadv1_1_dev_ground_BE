package io.devground.dbay.domain.order.order.model.vo;

public record CreateOrderResponse(
	String userCode,
	String orderCode,
	String nickName,
	String address,
	Long totalAmount
) {
}
