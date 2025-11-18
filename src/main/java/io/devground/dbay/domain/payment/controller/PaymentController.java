package io.devground.dbay.domain.payment.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.payment.mapper.PaymentMapper;
import io.devground.dbay.domain.payment.model.dto.request.ChargePaymentRequest;
import io.devground.dbay.domain.payment.model.dto.request.PaymentConfirmRequest;
import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.dto.request.TossPaymentRequest;
import io.devground.dbay.domain.payment.model.dto.response.OrderCodeResponse;
import io.devground.dbay.domain.payment.model.dto.response.PaymentResponse;
import io.devground.dbay.domain.payment.model.dto.response.TossPayResponse;
import io.devground.dbay.domain.payment.model.entity.Payment;
import io.devground.dbay.domain.payment.model.vo.PaymentDescription;
import io.devground.dbay.domain.payment.model.vo.PaymentStatus;
import io.devground.dbay.domain.payment.model.vo.PaymentType;
import io.devground.dbay.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

	@PostMapping("/toss")
	@Operation(summary = "", description = "")
	public BaseResponse<OrderCodeResponse> tossPayments(
		@RequestHeader("X-CODE") String userCode,
		@RequestBody @Valid TossPaymentRequest request
	) {
		//pending으로 미리 상태 저장해서 orderCode만 받아와야 함
		String orderCode = paymentService.getOrderCode(userCode, request.amount());
		OrderCodeResponse orderCodeResponse = new OrderCodeResponse(orderCode);

		return BaseResponse.success(
			200,
			orderCodeResponse,
			"결제를 성공적으로 처리하였습니다."
		);
	}

	@PostMapping("/process")
	public BaseResponse<String> processPayment(
		@RequestHeader("X-CODE") String userCode,
		@RequestBody @Valid PaymentConfirmRequest request
	) {
		PaymentRequest paymentRequest = new PaymentRequest(userCode, request.orderCode(), request.paymentKey(),
			request.amount(),
			PaymentStatus.COMPLETED);

		TossPayResponse payment = paymentService.payToss(paymentRequest, 0L);

		return BaseResponse.success(
			200,
			"결제를 성공적으로 처리하였습니다."
		);
	}

}