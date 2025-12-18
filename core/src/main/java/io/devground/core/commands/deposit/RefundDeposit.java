package io.devground.core.commands.deposit;

import io.devground.core.model.vo.DepositHistoryType;

public record RefundDeposit(
	String userCode,
	Long amount,
	DepositHistoryType type
) {
}
