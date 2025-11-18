package io.devground.dbay.domain.payment.model.vo;

public record TossPaymentsRequest(
	String paymentKey,
	String orderId,
	String amount
) {
}
