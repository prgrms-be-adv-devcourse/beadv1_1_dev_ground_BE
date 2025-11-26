package io.devground.payment.model.dto.request;

import io.devground.payment.model.vo.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRequest {
	private String userCode;
	private PaymentType paymentType;
	private String paymentKey;
	private String orderCode;
	private Long amount;

	public PaymentRequest(String userCode, PaymentType paymentType, String orderCode, Long amount) {
		this.userCode = userCode;
		this.paymentType = paymentType;
		this.orderCode = orderCode;
		this.amount = amount;
	}
}
