package io.devground.payment.model.dto.request;

public record RefundRequest(
	String userCode,
	String orderCode,
	Long amount
) {
}
