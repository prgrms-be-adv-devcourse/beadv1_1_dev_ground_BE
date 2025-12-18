package io.devground.payments.settlement.model.dto.response;

/**
 * 정산 통계 응답 DTO
 */
public record SettlementStatsResponse(
	Long totalCount,
	Long totalSettlementAmount,
	Long totalSettlementBalance,
	Long successCount,
	Long failedCount,
	Long processingCount
) {
}
