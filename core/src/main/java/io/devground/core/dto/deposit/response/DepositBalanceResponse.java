package io.devground.core.dto.deposit.response;

import lombok.Builder;

@Builder
public record DepositBalanceResponse(
	String userCode,
	Long balance
) {
}
