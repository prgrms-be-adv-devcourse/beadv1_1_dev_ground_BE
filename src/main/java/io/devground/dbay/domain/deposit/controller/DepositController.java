package io.devground.dbay.domain.deposit.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.deposit.dto.request.ChargeRequest;
import io.devground.dbay.domain.deposit.dto.request.RefundRequest;
import io.devground.dbay.domain.deposit.dto.request.WithdrawRequest;
import io.devground.dbay.domain.deposit.dto.response.DepositBalanceResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositHistoryResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositResponse;
import io.devground.dbay.domain.deposit.entity.vo.DepositHistoryType;
import io.devground.dbay.domain.deposit.service.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "DepositController", description = "예치금 관리 API")
@RequestMapping("/api/deposits")
public class DepositController {

	private final DepositService depositService;

	@PostMapping
	@Operation(summary = "예치금 계정 생성", description = "사용자의 예치금 계정을 생성합니다.")
	public BaseResponse<DepositResponse> createDeposit(@RequestHeader("X-CODE") String userCode) {
		DepositResponse response = depositService.createDeposit(userCode);

		return BaseResponse.success(201, response, "예치금 계정이 생성되었습니다.");
	}

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

	@PatchMapping("/charge")
	@Operation(summary = "예치금 충전", description = "사용자의 예치금 계정에 금액을 충전합니다.")
	public BaseResponse<DepositHistoryResponse> chargeDeposit(@RequestHeader("X-CODE") String userCode,
		@Valid @RequestBody ChargeRequest request) {

		DepositHistoryResponse response = depositService.charge(
			userCode,
			DepositHistoryType.CHARGE_TOSS,
			request.amount()
		);

		return BaseResponse.success(200, response, "충전이 완료되었습니다.");
	}

	@PostMapping("/withdraw")
	@Operation(summary = "예치금 인출", description = "사용자의 예치금 계정에서 금액을 인출합니다.")
	public BaseResponse<DepositHistoryResponse> withdrawDeposit(@RequestHeader("X-CODE") String userCode,
		@Valid @RequestBody WithdrawRequest request) {

		DepositHistoryResponse response = depositService.withdraw(
			userCode,
			DepositHistoryType.PAYMENT_TOSS,
			request.amount()
		);

		return BaseResponse.success(200, response, "인출이 완료되었습니다.");
	}

	@PostMapping("/refund")
	@Operation(summary = "예치금 환불", description = "사용자의 예치금 계정으로 금액을 환불합니다.")
	public BaseResponse<DepositHistoryResponse> refundDeposit(@RequestHeader("X-CODE") String userCode,
		@Valid @RequestBody RefundRequest request) {

		DepositHistoryResponse response = depositService.refund(
			userCode,
			DepositHistoryType.REFUND_TOSS,
			request.amount()
		);
		return BaseResponse.success(200, response, "환불이 완료되었습니다.");
	}

	@DeleteMapping
	@Operation(summary = "예치금 환불", description = "사용자의 예치금 계정으로 금액을 환불합니다.")
	public BaseResponse<String> deleteDeposit(@RequestHeader("X-CODE") String userCode) {
		depositService.deleteDeposit(userCode);

		return BaseResponse.success(204, "환불이 완료되었습니다.");
	}
}
