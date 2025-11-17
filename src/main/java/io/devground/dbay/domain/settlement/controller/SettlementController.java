package io.devground.dbay.domain.settlement.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.settlement.model.dto.response.SettlementResponse;
import io.devground.dbay.domain.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settlements")
public class SettlementController {

	private final SettlementService settlementService;

	/**
	 * 판매자별 정산 내역 조회
	 * GET /api/settlements/seller?page=0&size=20
	 */
	@GetMapping("/seller")
	public BaseResponse<Page<SettlementResponse>> getSettlementsBySeller(
		@RequestHeader("X-CODE") String sellerCode,
		@PageableDefault Pageable pageable) {

		Page<SettlementResponse> settlements = settlementService.getSettlementsBySeller(sellerCode, pageable);
		return BaseResponse.success(200, settlements, "판매자 정산 내역 조회 성공");
	}

}
