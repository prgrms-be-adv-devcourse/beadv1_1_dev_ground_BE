package io.devground.core.commands.deposit;

public record WithdrawDeposit(
	String userCode,
	Long amount,
	String type,
	String orderCode
) {
}
