package io.devground.payment.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TossPaymentConfirmRequest(
	String userCode,

	@NotBlank
	String orderCode,

	@Positive
	Long amount,

	String paymentKey
) {
}
