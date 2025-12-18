package io.devground.payments.payment.model.dto.request;

public record TossRefundRequest(
	String userCode,
	String paymentKey,
	Long amount
) {
}
