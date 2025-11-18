package io.devground.core.event.payment;

public record PaymentCreatedFailed(
	String orderCode,
	String userCode,
	String msg
) {
}
