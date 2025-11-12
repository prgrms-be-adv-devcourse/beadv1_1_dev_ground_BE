package io.devground.dbay.domain.order.order.model.vo;

import java.time.LocalDateTime;

public record PaidOrderResponse(
	String orderCode,
	LocalDateTime updatedAt
) {
}
