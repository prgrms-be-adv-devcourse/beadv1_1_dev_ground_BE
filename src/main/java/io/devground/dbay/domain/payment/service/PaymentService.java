package io.devground.dbay.domain.payment.service;

import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.entity.Payment;

public interface PaymentService {

	//결제
	Payment pay(String userCode, PaymentRequest payRequest);

	//환불
	Payment refund(String userCode, PaymentRequest payRequest);

	//Toss결제 승인
	Payment confirmPayment(PaymentRequest payRequest) throws Exception;

	//Toss결제 요청
	void payToss(PaymentRequest payRequest, Long balance);
}