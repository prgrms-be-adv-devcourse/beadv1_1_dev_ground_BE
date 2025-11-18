package io.devground.dbay.domain.payment.model.dto.request;

import io.devground.dbay.domain.payment.model.vo.PaymentStatus;
import io.devground.dbay.domain.payment.model.vo.PaymentType;

public record PaymentRequest(
	String userCode,
	String orderCode,
	String paymentKey,
	Long totalAmount,
	PaymentStatus status
) {
}