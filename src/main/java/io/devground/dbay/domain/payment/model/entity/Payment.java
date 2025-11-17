package io.devground.dbay.domain.payment.model.entity;

import java.time.LocalDateTime;

import io.devground.core.model.entity.BaseEntity;
import io.devground.dbay.domain.payment.model.vo.PaymentStatus;
import io.devground.dbay.domain.payment.model.vo.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

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
	public Payment(Long amount, String orderCode, String paymentKey, LocalDateTime paidAt, PaymentStatus paymentStatus) {
		this.amount = amount;
		this.orderCode = orderCode;
		this.paymentKey = paymentKey;
		this.paidAt = paidAt;
		this.paymentType = PaymentType.DEPOSIT;
		this.paymentStatus = PaymentStatus.PENDING;
	}
}