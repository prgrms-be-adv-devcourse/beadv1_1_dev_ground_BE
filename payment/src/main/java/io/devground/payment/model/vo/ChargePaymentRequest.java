package io.devground.payment.model.vo;

public record ChargePaymentRequest(
	String userCode,
	Long amout
) {
}
