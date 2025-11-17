package io.devground.dbay.domain.payment.mapper;

import java.time.LocalDateTime;

import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.entity.Payment;
import io.devground.dbay.domain.payment.model.vo.PaymentStatus;
import io.devground.dbay.domain.payment.model.vo.PaymentType;

public class PaymentMapper {
	public static Payment toEntity(PaymentRequest paymentRequest) {
		return Payment.builder()
			.amount(paymentRequest.totalAmount())
			.orderCode(paymentRequest.orderCode())
			.paidAt(LocalDateTime.now())
			.paymentStatus(PaymentStatus.PENDING)
			.paymentType(PaymentType.DEPOSIT)
			.build();
	}
}