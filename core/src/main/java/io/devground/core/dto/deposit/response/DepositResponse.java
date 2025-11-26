package io.devground.core.dto.deposit.response;

import java.time.LocalDateTime;

public record DepositResponse(
	Long id,
	String userCode,
	String depositCode,
	Long balance,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
