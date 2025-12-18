package io.devground.core.commands.deposit;

import java.util.List;

import io.devground.core.model.vo.DepositHistoryType;

public record WithdrawDeposit(
	String userCode,
	Long amount,
	DepositHistoryType type,
	String orderCode,
	List<String> productCodes
) {
}
