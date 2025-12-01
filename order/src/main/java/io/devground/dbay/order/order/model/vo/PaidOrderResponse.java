package io.devground.dbay.order.order.model.vo;

import java.time.LocalDateTime;

public record PaidOrderResponse(
	String orderCode,
	LocalDateTime updatedAt
) {
}
