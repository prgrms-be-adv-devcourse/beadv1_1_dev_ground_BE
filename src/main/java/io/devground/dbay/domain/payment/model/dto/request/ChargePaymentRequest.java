package io.devground.dbay.domain.payment.model.dto.request;

public record ChargePaymentRequest(
	String userCode,
	Long amount
) {
}
