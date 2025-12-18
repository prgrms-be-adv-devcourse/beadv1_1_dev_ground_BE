package io.devground.core.event.payment;

public record CancelCreatePaymentEvent(
	String userCode,
	String orderCode,
	String msg
) {
}
