package io.devground.core.dto.deposit.response;

import java.time.LocalDateTime;

import io.devground.core.model.vo.DepositHistoryType;
import lombok.Builder;

@Builder
public record DepositHistoryResponse(
	String code,
	String depositCode,
	String userCode,
	String payerDepositCode,
	String payeeDepositCode,
	Long amount,
	Long balanceAfter,
	DepositHistoryType type,
	String description,
	LocalDateTime createdAt
) {
}
