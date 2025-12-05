package io.devground.core.event.deposit;

public record DepositChargeFailed(
	String userCode,
	String paymentKey,
	Long amount,
	String msg
) {
}
