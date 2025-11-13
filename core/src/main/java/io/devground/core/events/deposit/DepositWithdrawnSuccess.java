package io.devground.core.events.deposit;

public record DepositWithdrawnSuccess(
	String userCode,
	String depositHistoryCode,
	Long amount,
	Long balanceAfter
) {
}
