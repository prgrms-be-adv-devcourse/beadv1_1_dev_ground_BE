package io.devground.payments.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.devground.payments.payment.model.dto.request.PaymentRequest;
import io.devground.payments.payment.model.dto.request.RefundRequest;
import io.devground.payments.payment.model.dto.request.TossRefundRequest;
import io.devground.payments.payment.model.dto.response.GetPaymentsResponse;
import io.devground.payments.payment.model.entity.Payment;
import io.devground.payments.payment.model.vo.PaymentConfirmRequest;

public interface PaymentService {
	Payment process(String userCode, PaymentConfirmRequest request);

	Payment pay(PaymentRequest request);

	void refund(RefundRequest request);

	void tossRefund(TossRefundRequest request);

	Payment confirmPayment(PaymentRequest request) throws Exception;

	Page<GetPaymentsResponse> getPayments(String userCode, Pageable pageable);

	void applyDepositPayment(String orderCode);

	void applyDepositCharge(String userCode);

	void cancelDepositPayment(String orderCode);
}
