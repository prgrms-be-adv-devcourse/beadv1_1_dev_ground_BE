package io.devground.core.dto.deposit.response;

import java.time.LocalDateTime;

import io.devground.core.model.vo.DepositHistoryType;

public record DepositHistoryResponse(
	Long id,
	String code,
	Long depositId,
	String userCode,
	Long payerDepositId,
	Long payeeDepositId,
	Long amount,
	Long balanceAfter,
	DepositHistoryType type,
	String description,
	LocalDateTime createdAt
) {
}
