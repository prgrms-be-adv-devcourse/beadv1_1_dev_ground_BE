package io.devground.core.event.deposit;

public record DepositRefundFailed(
	String userCode,
	Long amount,
	String msg
) {
}
