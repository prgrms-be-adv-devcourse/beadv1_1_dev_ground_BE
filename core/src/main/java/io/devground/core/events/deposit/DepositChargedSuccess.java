package io.devground.core.events.deposit;

public record DepositChargedSuccess(
	String userCode,
	String depositHistoryCode,
	Long amount,
	Long balanceAfter
) {
}
