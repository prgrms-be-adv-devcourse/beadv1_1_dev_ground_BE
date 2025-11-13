package io.devground.core.event.order.Temp.event;

import io.devground.core.model.vo.DepositHistoryType;

public record PaymentCreatedEvent(
	String userCode,
	Long amount,
	DepositHistoryType type,
	String orderCode
) {
}
