package io.devground.core.events.deposit;

public record DepositRefundedSuccess(
	String userCode,
	String depositHistoryCode,
	Long amount,
	Long balanceAfter
) {
}
