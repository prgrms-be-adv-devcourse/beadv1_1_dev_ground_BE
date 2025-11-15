package io.devground.dbay.domain.payment.repository;

import org.springframework.stereotype.Repository;

import io.devground.dbay.domain.payment.model.dto.response.PaymentResponse;

@Repository
public interface PaymentRepository {

	PaymentResponse findByUserCode (String userCode);
}