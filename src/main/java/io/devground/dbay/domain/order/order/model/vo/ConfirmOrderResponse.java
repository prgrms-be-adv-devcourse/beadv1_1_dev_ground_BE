package io.devground.dbay.domain.order.order.model.vo;

import java.time.LocalDateTime;

public record ConfirmOrderResponse(
	String orderCode,
	LocalDateTime updatedAt
) {
}
