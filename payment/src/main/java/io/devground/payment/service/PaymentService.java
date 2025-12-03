package io.devground.payment.service;

import io.devground.payment.model.dto.request.PaymentRequest;
import io.devground.payment.model.entity.Payment;
import io.devground.payment.model.vo.PaymentConfirmRequest;

public interface PaymentService {
	Payment process(String userCode, PaymentConfirmRequest request);

	Payment pay(PaymentRequest request);

	Payment refund(String userCode, PaymentRequest request);

	Payment confirmPayment(PaymentRequest request) throws Exception;

	void applyDepositPayment(String orderCode);

	void applyDepositCharge(String userCode);

	void cancelDepositPayment(String orderCode);
}
