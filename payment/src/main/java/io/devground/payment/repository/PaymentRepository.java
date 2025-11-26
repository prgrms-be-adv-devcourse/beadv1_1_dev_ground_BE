package io.devground.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.payment.model.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	Optional<Payment> findByOrderCode(String orderCode);
}
