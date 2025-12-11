package io.devground.payments.payment.model.entity;

import java.time.LocalDateTime;

import io.devground.core.model.entity.BaseEntity;

import io.devground.payments.payment.model.vo.PaymentStatus;
import io.devground.payments.payment.model.vo.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	private String orderCode;

	@Setter
	@Column(nullable = false, columnDefinition = "VARCHAR(36)")
	private String userCode;

	private Long amount;

	@Setter
	private PaymentType paymentType;

	@Setter
	private PaymentStatus paymentStatus;

	@Setter
	private String paymentKey;

	private LocalDateTime paidAt;

	@Builder
	public Payment(String userCode, Long amount, String orderCode, String paymentKey, PaymentStatus paymentStatus) {
		this.userCode = userCode;
		this.amount = amount;
		this.orderCode = orderCode;
		this.paymentKey = paymentKey;
		this.paidAt = LocalDateTime.now();
		this.paymentType = PaymentType.DEPOSIT;
		this.paymentStatus = PaymentStatus.PAYMENT_PENDING;
	}
}