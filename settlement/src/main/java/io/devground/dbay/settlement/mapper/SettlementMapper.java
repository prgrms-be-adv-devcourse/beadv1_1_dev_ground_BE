package io.devground.dbay.settlement.mapper;

import io.devground.dbay.settlement.model.dto.response.SettlementResponse;
import io.devground.dbay.settlement.model.entity.Settlement;

public class SettlementMapper {

	public static SettlementResponse toSettlementResponse(Settlement settlement) {
		return new SettlementResponse(
			settlement.getId(),
			settlement.getCode(),
			settlement.getOrderItemCode(),
			settlement.getSettlementStatus(),
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
