package io.devground.payments.payment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.payments.payment.model.entity.Payment;
import io.devground.payments.payment.model.vo.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	Optional<Payment> findByOrderCode(String orderCode);
	Optional<Payment> findByPaymentKey(String paymentKey);
	Optional<Payment> findByUserCodeAndPaymentStatus(String userCode, PaymentStatus paymentStatus);

	Page<Payment> findByUserCodeOrderByPaidAtDesc(String userCode, Pageable pageable);
}
