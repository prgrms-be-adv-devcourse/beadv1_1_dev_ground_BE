package io.devground.payments.settlement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.devground.payments.settlement.model.dto.request.CreateSettlementRequest;
import io.devground.payments.settlement.model.dto.response.SettlementResponse;

public interface SettlementService {

	/**
	 * 판매자별 정산 내역 조회
	 */
	Page<SettlementResponse> getSettlementsBySeller(String sellerCode, Pageable pageable);

	/**
	 * 정산 생성
	 */
	SettlementResponse createSettlement(CreateSettlementRequest request);
}
