package io.devground.dbay.domain.deposit.dto.response;

import java.time.LocalDateTime;

public record DepositResponse(
	Long id,
	String userCode,
	Long balance,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
