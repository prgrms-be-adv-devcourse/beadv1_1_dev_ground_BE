package io.devground.dbay.order.order.model.vo;

import java.time.LocalDateTime;
import java.util.List;

public record GetOrdersResponse(
	String code,
	LocalDateTime createdAt,
	Long totalAmount,
	OrderStatus orderStatus,
	List<OrderItemInfo> items
) {
}
