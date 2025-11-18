package io.devground.core.event.deposit;

public record DepositChargeFailed(
	String userCode,
	Long amount,
	String msg
) {
}
