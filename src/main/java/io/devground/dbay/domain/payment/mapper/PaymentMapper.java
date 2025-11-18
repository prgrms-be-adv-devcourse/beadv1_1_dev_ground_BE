package io.devground.dbay.domain.payment.mapper;

import io.devground.dbay.domain.payment.model.entity.Payment;
import io.devground.dbay.domain.payment.model.vo.PaymentDescription;

public class PaymentMapper {
	public static PaymentDescription toDescription(Payment payment) {
		return new PaymentDescription(
			payment.getOrderCode(),
			payment.getCode()
		);
	}
}