package io.devground.core.commands.payment;

import java.util.List;

public record PaymentCreateCommand(
	String userCode,
	String orderCode,
	Long totalAmount,
	List<String> productCodes
) {
}
