package io.devground.dbay.domain.payment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.payment.mapper.PaymentMapper;
import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.entity.Payment;
import io.devground.dbay.domain.payment.model.vo.DepositPaymentRequest;
import io.devground.dbay.domain.payment.model.vo.PaymentConfirmRequest;
import io.devground.dbay.domain.payment.model.vo.PaymentDescription;
import io.devground.dbay.domain.payment.model.vo.PaymentType;
import io.devground.dbay.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/process")
	public BaseResponse<PaymentDescription> processPayment(
		//            @AuthenticationPrincipal(expression = "accountCode") String accountCode,
		@RequestHeader("X-CODE") String accountCode,
		@RequestBody @Valid PaymentConfirmRequest request
	) {

		Payment payment = paymentService.process(accountCode, request);

		return BaseResponse.success(
			200,
			PaymentMapper.toDescription(payment),
			"결제를 성공적으로 처리하였습니다."
		);

	}

	@PostMapping("/toss")
	public BaseResponse<PaymentDescription> tossPayment(
		//            @AuthenticationPrincipal(expression = "accountCode") String accountCode,
		@RequestHeader("X-CODE") String accountCode,
		@RequestBody @Valid PaymentConfirmRequest request
	) {

		Payment payment = paymentService.pay(
			new PaymentRequest(accountCode, PaymentType.TOSS_PAYMENT, request.paymentKey(), request.orderCode(), request.amount())
		);

		return BaseResponse.success(
			200,
			PaymentMapper.toDescription(payment),
			"결제를 성공적으로 처리하였습니다."
		);
	}

	@PostMapping("/deposit")
	public BaseResponse<PaymentDescription> depositPayment(
		//            @AuthenticationPrincipal(expression = "accountCode") String accountCode,
		@RequestHeader("X-CODE") String accountCode,
		@RequestBody @Valid DepositPaymentRequest request
	) {

		Payment payment = paymentService.pay(
			new PaymentRequest(accountCode, PaymentType.DEPOSIT, null, request.orderCode(), request.amount())
		);

		return BaseResponse.success(
			200,
			PaymentMapper.toDescription(payment),
			"결제를 성공적으로 처리하였습니다."
		);

	}


}
