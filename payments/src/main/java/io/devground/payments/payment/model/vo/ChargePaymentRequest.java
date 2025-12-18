package io.devground.payments.payment.model.vo;

public record ChargePaymentRequest(
	String userCode,
	Long amout
) {
}
