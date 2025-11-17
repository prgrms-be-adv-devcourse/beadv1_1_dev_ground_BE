package io.devground.dbay.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.devground.dbay.domain.payment.model.dto.response.PaymentResponse;
import io.devground.dbay.domain.payment.model.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	PaymentResponse findByUserCode(String userCode);
}