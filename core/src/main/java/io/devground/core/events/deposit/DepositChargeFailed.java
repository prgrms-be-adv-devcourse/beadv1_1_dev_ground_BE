package io.devground.core.events.deposit;

public record DepositChargeFailed(
	String userCode,
	Long amount,
	String msg
) {
}
