package io.devground.dbay.domain.order.order.model.vo;

import java.time.LocalDateTime;

public record GetOrderDetailResponse(
	String orderCode,
	LocalDateTime createdAt,
	OrderStatus orderStatus,
	Long totalAmount,
	Long discount,
	Long productTotalAmount,
	int shippingFee,
	String userNickName,
	String userAddress,
	boolean cancellable
) {
}
