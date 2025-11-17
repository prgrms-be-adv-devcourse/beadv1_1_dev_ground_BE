package io.devground.dbay.domain.payment.model.dto.request;

import io.devground.dbay.domain.payment.model.vo.PaymentStatus;

public record PaymentRequest(
	String userCode,
	String orderCode,
	String paymentKey,
	Long totalAmount,
	PaymentStatus status
) {
}