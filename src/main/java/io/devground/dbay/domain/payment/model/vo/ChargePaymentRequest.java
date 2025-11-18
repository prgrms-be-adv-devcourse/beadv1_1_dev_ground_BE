package io.devground.dbay.domain.payment.model.vo;

public record ChargePaymentRequest(
	String userCode,
	Long amout
) {
}
