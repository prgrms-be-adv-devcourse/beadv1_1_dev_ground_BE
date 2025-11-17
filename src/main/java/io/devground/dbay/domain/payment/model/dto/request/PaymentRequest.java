package io.devground.dbay.domain.payment.model.dto.request;

import java.time.LocalDateTime;

public record PaymentRequest(
	String userCode,
	String orderCode,
	String paymentKey,
	Long totalAmount
) {
}