package io.devground.dbay.domain.payment.model.vo;

public record PaymentDescription(
	String orderCode,
	String paymentCode
) {
}
