package io.devground.core.event.deposit;

public record DepositChargedSuccess(
	String userCode,
	String depositHistoryCode,
	Long amount,
	Long balanceAfter
) {
}
