package io.devground.dbay.domain.payment.model.dto.request;

public record TossPayRequest(
	String orderCode,
	String paymentKey,
	Long chargeAmount
) {
}