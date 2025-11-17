package io.devground.core.event.payment;

import io.devground.core.model.vo.DepositHistoryType;

public record PaymentChargeDeposit(
	String userCode,
	Long amount
) {
}
