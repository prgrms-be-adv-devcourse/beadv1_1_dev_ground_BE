package io.devground.payments.payment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;

import io.devground.payments.payment.mapper.PaymentMapper;
import io.devground.payments.payment.model.dto.request.PaymentRequest;
import io.devground.payments.payment.model.dto.request.TossRefundRequest;
import io.devground.payments.payment.model.dto.response.GetPaymentsResponse;
import io.devground.payments.payment.model.entity.Payment;
import io.devground.payments.payment.model.vo.DepositPaymentRequest;
import io.devground.payments.payment.model.vo.PaymentConfirmRequest;
import io.devground.payments.payment.model.vo.PaymentDescription;
import io.devground.payments.payment.model.vo.PaymentType;
import io.devground.payments.payment.model.vo.TossPaymentConfirmRequest;
import io.devground.payments.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
	private final PaymentService paymentService;

	@PostMapping("/process")
	public BaseResponse<PaymentDescription> processPayment(
		@RequestHeader("X-CODE") String userCode,
		@RequestBody @Valid PaymentConfirmRequest request
	) {

		Payment payment = paymentService.process(userCode, request);

		return BaseResponse.success(
			200,
			PaymentMapper.toDescription(payment),
			"결제를 성공적으로 처리하였습니다."
		);

	}

	@PostMapping("/toss")
	public BaseResponse<PaymentDescription> tossPayment(
		@RequestBody @Valid TossPaymentConfirmRequest request
	) {
		log.info(request.paymentKey());
		Payment payment = paymentService.pay(
			new PaymentRequest(request.userCode(), PaymentType.TOSS_PAYMENT, request.paymentKey(), request.orderCode(), request.amount())
		);

		return BaseResponse.success(
			200,
			PaymentMapper.toDescription(payment),
			"결제를 성공적으로 처리하였습니다."
		);
	}

	@PostMapping("/deposit")
	public BaseResponse<PaymentDescription> depositPayment(
		@RequestHeader("X-CODE") String userCode,
		@RequestBody @Valid DepositPaymentRequest request
	) {

		Payment payment = paymentService.pay(
			new PaymentRequest(userCode, PaymentType.DEPOSIT, null, request.orderCode(), request.amount())
		);

		return BaseResponse.success(
			200,
			PaymentMapper.toDescription(payment),
			"결제를 성공적으로 처리하였습니다."
		);
	}

	@GetMapping("/")
	public BaseResponse<Page<GetPaymentsResponse>> getPayments(
		@RequestHeader("X-CODE") String userCode,
		@PageableDefault Pageable pageable
	){
		return BaseResponse.success(200, paymentService.getPayments(userCode, pageable), "결제 내역 조회 성공");
	}

	@PostMapping("/tossRefund/{userCode}/{paymentKey}")
	public BaseResponse<String> refundPayment(
		@PathVariable String userCode,
		@PathVariable String paymentKey
	){
		TossRefundRequest request = new TossRefundRequest(userCode, paymentKey, 10000L);
		paymentService.tossRefund(request);

		return BaseResponse.success(200, "환불 성공");
	}
}
