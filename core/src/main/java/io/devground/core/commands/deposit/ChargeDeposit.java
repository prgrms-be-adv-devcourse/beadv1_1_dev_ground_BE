package io.devground.core.commands.deposit;

import io.devground.core.model.vo.DepositHistoryType;

public record ChargeDeposit(
	String userCode,
	Long amount,
	DepositHistoryType type
) {
}
