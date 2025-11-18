package io.devground.core.event.deposit;

public record DepositRefundedSuccess(
	String userCode,
	String depositHistoryCode,
	Long amount,
	Long balanceAfter
) {
}
