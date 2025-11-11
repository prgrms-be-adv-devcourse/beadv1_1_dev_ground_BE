package io.devground.dbay.domain.deposit.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositHistoryResponse;
import io.devground.dbay.domain.deposit.entity.DepositHistory;
import io.devground.dbay.domain.deposit.entity.vo.DepositHistoryType;
import io.devground.dbay.domain.deposit.mapper.DepositMapper;
import io.devground.dbay.domain.deposit.service.DepositService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deposit-histories")
public class DepositHistoryController {

	private final DepositService depositService;

	@GetMapping
	public BaseResponse<Page<DepositHistoryResponse>> getDepositHistories(
		@RequestHeader("X-CODE") String userCode,
		@RequestParam(required = false) DepositHistoryType type,
		@PageableDefault Pageable pageable) {

		Page<DepositHistory> histories = depositService.getDepositHistories(userCode, pageable);
		Page<DepositHistoryResponse> response = histories.map(DepositMapper::toDepositHistoryResponse);

		return BaseResponse.success(200, response, "거래 내역 조회 성공");
	}

	@GetMapping("/{historyCode}")
	public BaseResponse<DepositHistoryResponse> getDepositHistory(@PathVariable String historyCode) {
		DepositHistoryResponse response = depositService.getDepositHistoryByCode(historyCode);

		return BaseResponse.success(200, response, "거래 내역 조회 성공");
	}
}
