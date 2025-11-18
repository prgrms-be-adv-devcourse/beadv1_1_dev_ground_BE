package io.devground.dbay.domain.payment.model.vo;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentConfirmRequest(

	@NotBlank
	String orderCode,

	@NotNull
	Boolean useDeposit,

	@Positive
	Long amount,

	String paymentKey,

	List<String> productCodes
) {
}