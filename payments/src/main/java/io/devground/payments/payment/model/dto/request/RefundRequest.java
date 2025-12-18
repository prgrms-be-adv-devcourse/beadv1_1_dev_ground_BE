package io.devground.payments.payment.model.dto.request;

public record RefundRequest(
	String userCode,
	String orderCode,
	Long amount
) {
}
