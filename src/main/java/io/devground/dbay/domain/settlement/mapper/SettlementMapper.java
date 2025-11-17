package io.devground.dbay.domain.settlement.mapper;

import io.devground.dbay.domain.settlement.model.dto.response.SettlementResponse;
import io.devground.dbay.domain.settlement.model.entity.Settlement;

public class SettlementMapper {

	public static SettlementResponse toSettlementResponse(Settlement settlement) {
		return new SettlementResponse(
			settlement.getId(),
			settlement.getCode(),
			settlement.getOrderItemCode(),
			settlement.getSettlementStatus(),
			settlement.getDepositHistoryCode(),
			settlement.getBuyerCode(),
			settlement.getSellerCode(),
			settlement.getSettlementDate(),
			settlement.getSettlementRate(),
			settlement.getTotalAmount(),
			settlement.getSettlementAmount(),
			settlement.getSettlementBalance(),
			settlement.getCreatedAt(),
			settlement.getUpdatedAt()
		);
	}
}