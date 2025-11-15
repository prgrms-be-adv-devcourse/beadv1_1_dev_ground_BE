package io.devground.dbay.domain.payment.model.dto.request;

public record PayRequest(
	String userCode,
	String orderCode,
	Long totalAmount
) {
}
