package io.devground.payments.payment.mapper;

import io.devground.payments.payment.model.entity.Payment;
import io.devground.payments.payment.model.vo.PaymentDescription;

public class PaymentMapper {
	public static PaymentDescription toDescription(Payment payment) {
		return new PaymentDescription(
			payment.getOrderCode(),
			payment.getCode()
		);
	}
}
