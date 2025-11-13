package io.devground.core.events.deposit;

public record DepositRefundFailed(
	String userCode,
	Long amount,
	String msg
) {
}
