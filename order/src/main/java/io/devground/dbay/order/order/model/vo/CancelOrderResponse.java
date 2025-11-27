package io.devground.dbay.order.order.model.vo;

import java.time.LocalDateTime;

public record CancelOrderResponse(
	String orderCode,
	LocalDateTime updatedAt
) {
}
