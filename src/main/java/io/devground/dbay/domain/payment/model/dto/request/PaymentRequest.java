package io.devground.dbay.domain.payment.model.dto.request;

import java.time.LocalDateTime;

public record PaymentRequest(
	Long amount,
	String accountHistoryCode,
	LocalDateTime paidAt
) {
}