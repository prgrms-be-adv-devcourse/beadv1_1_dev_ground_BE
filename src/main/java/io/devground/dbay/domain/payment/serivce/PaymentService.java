package io.devground.dbay.domain.payment.serivce;

import io.devground.dbay.domain.payment.model.dto.request.PayRequest;
import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.dto.request.TossPayRequest;
import io.devground.dbay.domain.payment.model.entity.Payment;

public interface PaymentService {


	//결제
	Payment pay (String userCode, PayRequest payRequest);

	//환불
	Payment refund (String userCode, PayRequest payRequest);

	//Toss결제 승인
	Payment confirmPayment (PayRequest payRequest) throws Exception;

	//Toss결제 요청
	Payment payToss(PayRequest payRequest, Long balance);
}
