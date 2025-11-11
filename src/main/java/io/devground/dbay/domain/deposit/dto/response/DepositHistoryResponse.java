package io.devground.dbay.domain.deposit.dto.response;

import java.time.LocalDateTime;

import io.devground.dbay.domain.deposit.entity.vo.DepositHistoryType;

public record DepositHistoryResponse(
	Long id,
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
