package io.devground.payment.model.dto.request;

public record TossRefundRequest(
	String userCode,
	String paymentKey,
	Long amount
) {
}
