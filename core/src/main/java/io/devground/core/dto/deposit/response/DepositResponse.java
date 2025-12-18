package io.devground.core.dto.deposit.response;

import java.time.LocalDateTime;

import lombok.Builder;

//todo : deposit 스펙 바뀜
@Builder
public record DepositResponse(
	String userCode,
	String depositCode,
	Long balance,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
