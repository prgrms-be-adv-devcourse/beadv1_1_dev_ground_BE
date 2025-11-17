package io.devground.dbay.domain.payment.model.dto.response;

import io.devground.core.model.vo.DepositHistoryType;
import io.devground.dbay.domain.payment.model.vo.PaymentStatus;

public record PaymentResponse(
	String userCode,
	Long amount,
	DepositHistoryType type,
	String orderCode,
	PaymentStatus status
) {
}