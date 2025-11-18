package io.devground.core.event.payment;

public record CreatePaymentEvent(
	String userCode,
	String orderCode,
	Long totalAmount
) {
}
