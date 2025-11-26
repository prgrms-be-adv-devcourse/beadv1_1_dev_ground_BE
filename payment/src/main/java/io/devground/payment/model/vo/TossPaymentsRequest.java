package io.devground.payment.model.vo;

public record TossPaymentsRequest(
	String paymentKey,
	String orderId,
	String amount
) {
}
