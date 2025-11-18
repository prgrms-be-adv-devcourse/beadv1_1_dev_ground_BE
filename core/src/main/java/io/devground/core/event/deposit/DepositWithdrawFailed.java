package io.devground.core.event.deposit;

public record DepositWithdrawFailed(
	String userCode,
	Long amount,
	String msg,
	String orderCode
) {
}
