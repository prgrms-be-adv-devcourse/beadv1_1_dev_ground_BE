package io.devground.dbay.settlement.model.dto.response;

import java.time.LocalDateTime;

import io.devground.dbay.settlement.model.entity.vo.SettlementStatus;

/**
 * 정산 정보 응답 DTO
 */
public record SettlementResponse(
	Long id,
	String code,
	String orderItemCode,
	SettlementStatus settlementStatus,
	String buyerCode,
	String sellerCode,
	LocalDateTime settlementDate,
	Double settlementRate,
	Long totalAmount,
	Long settlementAmount,
	Long settlementBalance,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
