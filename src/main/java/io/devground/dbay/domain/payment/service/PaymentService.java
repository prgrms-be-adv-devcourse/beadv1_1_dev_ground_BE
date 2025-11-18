package io.devground.dbay.domain.payment.service;

import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.entity.Payment;
import io.devground.dbay.domain.payment.model.vo.PaymentConfirmRequest;

public interface PaymentService {
	Payment process(String accountCode, PaymentConfirmRequest request);

	Payment pay(PaymentRequest request);

	Payment refund(String accountCode, PaymentRequest request);

	Payment confirmPayment(PaymentRequest request) throws Exception;
}