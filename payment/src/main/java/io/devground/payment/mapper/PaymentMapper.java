package io.devground.payment.mapper;

import io.devground.payment.model.entity.Payment;
import io.devground.payment.model.vo.PaymentDescription;

public class PaymentMapper {
	public static PaymentDescription toDescription(Payment payment) {
		return new PaymentDescription(
			payment.getOrderCode(),
			payment.getCode()
		);
	}
}
