package io.devground.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.devground.payment.model.dto.request.PaymentRequest;
import io.devground.payment.model.dto.request.RefundRequest;
import io.devground.payment.model.dto.response.GetPaymentsResponse;
import io.devground.payment.model.entity.Payment;
import io.devground.payment.model.vo.PaymentConfirmRequest;

public interface PaymentService {
	Payment process(String userCode, PaymentConfirmRequest request);

	Payment pay(PaymentRequest request);

	void refund(RefundRequest request);

	Payment confirmPayment(PaymentRequest request) throws Exception;

	Page<GetPaymentsResponse> getPayments(String userCode, Pageable pageable);

	void applyDepositPayment(String orderCode);

	void applyDepositCharge(String userCode);

	void cancelDepositPayment(String orderCode);
}
