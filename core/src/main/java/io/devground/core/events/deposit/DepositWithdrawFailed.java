package io.devground.core.events.deposit;

public record DepositWithdrawFailed(
	String userCode,
	Long amount,
	String msg
) {
}
