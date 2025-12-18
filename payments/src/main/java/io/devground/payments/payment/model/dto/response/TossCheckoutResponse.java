package io.devground.payments.payment.model.dto.response;

public record TossCheckoutResponse(
	String userCode,
	String orderCode,
	String clientKey,
	Long amount
) {
}
