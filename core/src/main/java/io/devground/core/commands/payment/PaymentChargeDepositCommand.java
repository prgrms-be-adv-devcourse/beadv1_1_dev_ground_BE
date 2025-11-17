package io.devground.core.commands.payment;

import io.devground.core.model.vo.DepositHistoryType;

public record PaymentChargeDepositCommand(
	String userCode,
	Long totalAmount,
	String orderCode,
	DepositHistoryType type
) {
}
