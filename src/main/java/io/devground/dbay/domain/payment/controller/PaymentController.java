package io.devground.dbay.domain.payment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.dto.response.PaymentResponse;
import io.devground.dbay.domain.payment.model.dto.response.TossPayResponse;
import io.devground.dbay.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name = "PaymentController")
public class PaymentController {

	private final PaymentService paymentService;

	@GetMapping("/")
	@Operation(summary = "결제 내역 조회", description = "결제 내역을 조회하는 API 입니다.")
	public BaseResponse<Page<PaymentResponse>> getPayments(
		@RequestHeader("X-CODE") String userCode,
		@PageableDefault Pageable pageable) {

		return BaseResponse.success(200, "Toss Pay 거래 내역 조회 성공");
	}

	@PostMapping("/tossPay")
	@Operation(summary = "", description = "")
	public BaseResponse<TossPayResponse> tossPayments (
		@RequestBody PaymentRequest request
	){

		return BaseResponse.success(200, paymentService.payToss(request, 0L), "토스 결제에 성공했습니다.");
	}

}