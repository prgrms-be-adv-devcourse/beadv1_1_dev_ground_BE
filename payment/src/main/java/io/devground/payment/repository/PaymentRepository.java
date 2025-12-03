package io.devground.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.payment.model.entity.Payment;
import io.devground.payment.model.vo.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	Optional<Payment> findByOrderCode(String orderCode);
	Optional<Payment> findByUserCodeAndPaymentStatus(String userCode, PaymentStatus paymentStatus);
}
