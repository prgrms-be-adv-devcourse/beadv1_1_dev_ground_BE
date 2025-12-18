package io.devground.core.event.payment;

import java.util.List;

import io.devground.core.model.vo.DepositHistoryType;

public record PaymentCreatedEvent(
	String userCode,
	Long amount,
	DepositHistoryType type,
	String orderCode,
	List<String> productCodes
) {
}
