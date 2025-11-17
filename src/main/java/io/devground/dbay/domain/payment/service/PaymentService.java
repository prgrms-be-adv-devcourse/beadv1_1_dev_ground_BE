package io.devground.dbay.domain.payment.service;

import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.dto.response.TossPayResponse;
import io.devground.dbay.domain.payment.model.entity.Payment;

public interface PaymentService {

	//결제
	Payment pay(PaymentRequest payRequest);

	//환불
	Payment refund(String userCode, PaymentRequest payRequest);

	//Toss결제 승인
	Payment confirmPayment(PaymentRequest payRequest) throws Exception;

	//Toss결제 요청
	TossPayResponse payToss(PaymentRequest payRequest, Long balance);
}