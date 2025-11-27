package io.devground.dbay.order.order.model.vo;

import java.time.LocalDateTime;

public record ConfirmOrderResponse(
	String orderCode,
	LocalDateTime updatedAt
) {
}
