package io.devground.dbay.domain.payment.model.dto.response;

public record TossPayResponse(
	String paymentCode,
	String paymentKey
) {

}