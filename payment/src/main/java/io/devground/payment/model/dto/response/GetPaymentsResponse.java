package io.devground.payment.model.dto.response;

import java.time.LocalDateTime;

import io.devground.payment.model.vo.PaymentStatus;
import io.devground.payment.model.vo.PaymentType;

public record GetPaymentsResponse(
	String paymentCode,
	LocalDateTime createdAt,
	Long amount,
	PaymentType type,
	PaymentStatus status
) {
}
