package io.devground.dbay.domain.payment.model.dto.request;

public record PaymentConfirmRequest(
	String orderCode,
	String paymentKey,
	Long amount
) {
}
