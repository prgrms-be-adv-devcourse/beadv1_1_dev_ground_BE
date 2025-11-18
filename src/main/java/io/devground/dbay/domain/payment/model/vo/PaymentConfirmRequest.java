package io.devground.dbay.domain.payment.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentConfirmRequest(

	@NotBlank
	String orderCode,

	@NotNull
	Boolean useDeposit,

	// 만일 예치금을 사용한다면 사용하는 금액
	Long depositAmount,

	@Positive
	Long amount,

	String paymentKey

) {
}