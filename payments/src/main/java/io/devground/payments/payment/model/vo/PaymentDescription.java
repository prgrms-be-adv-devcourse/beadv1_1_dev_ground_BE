package io.devground.payments.payment.model.vo;

public record PaymentDescription(
	String orderCode,
	String paymentCode
) {
}
