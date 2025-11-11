package io.devground.dbay.domain.deposit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WithdrawRequest(
	@NotNull(message = "인출 금액은 필수입니다")
	@Positive(message = "인출 금액은 양수여야 합니다")
	Long amount
) {
}
