package io.devground.dbay.domain.settlement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.devground.dbay.domain.settlement.model.dto.response.SettlementResponse;

public interface SettlementService {

	/**
	 * 판매자별 정산 내역 조회
	 */
	Page<SettlementResponse> getSettlementsBySeller(String sellerCode, Pageable pageable);
}
