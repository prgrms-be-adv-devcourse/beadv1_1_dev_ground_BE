package io.devground.deposit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.dto.deposit.response.DepositBalanceResponse;
import io.devground.core.dto.deposit.response.DepositResponse;
import io.devground.core.model.web.BaseResponse;
import io.devground.deposit.service.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "DepositController", description = "예치금 관리 API")
@RequestMapping("/api/deposits")
public class DepositController {

	private final DepositService depositService;

	@GetMapping
	@Operation(summary = "예치금 계정 조회", description = "사용자의 예치금 계정 정보를 조회합니다.")
	public BaseResponse<DepositResponse> getDeposit(@RequestHeader("X-CODE") String userCode) {
		DepositResponse response = depositService.getByUserCode(userCode);

		return BaseResponse.success(200, response, "예치금 계정 조회 성공");
	}

	@GetMapping("/balance")
	@Operation(summary = "잔액 조회", description = "사용자의 현재 예치금 잔액을 조회합니다.")
	public BaseResponse<DepositBalanceResponse> getBalance(@RequestHeader("X-CODE") String userCode) {
		DepositBalanceResponse response = depositService.getByBalance(userCode);

		return BaseResponse.success(200, response, "잔액 조회 성공");
	}

}
