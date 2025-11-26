package io.devground.payment.model.vo;

public record PaymentDescription(
	String orderCode,
	String paymentCode
) {
}
