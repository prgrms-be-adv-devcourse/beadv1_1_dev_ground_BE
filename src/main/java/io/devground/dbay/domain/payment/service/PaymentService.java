package io.devground.dbay.domain.payment.service;

import io.devground.dbay.domain.payment.model.dto.request.ChargePaymentRequest;
import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.dto.request.TossPayRequest;
import io.devground.dbay.domain.payment.model.dto.response.PaymentResponse;
import io.devground.dbay.domain.payment.model.dto.response.TossPayResponse;
import io.devground.dbay.domain.payment.model.entity.Payment;

public interface PaymentService {

	//결제
	boolean pay(String userCode, String orderCode, Long totalAmount, String paymentKey);

	//환불
	Payment refund(String userCode, Long amount);

	//Toss결제 승인
	String confirmTossPayment(TossPayRequest tossPayRequest) throws Exception;

	//예치금 결제 성공
	void applyDepositPayment(String orderCode);

	void canceledDepositPayment(String orderCode);

	//Toss결제 요청
	TossPayResponse payToss(PaymentRequest payRequest, Long balance);

	Payment getByOrderCode(String orderCode);

	String getOrderCode(String userCode, Long totalAmount);
}