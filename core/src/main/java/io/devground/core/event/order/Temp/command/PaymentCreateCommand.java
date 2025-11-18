package io.devground.core.event.order.Temp.command;

import java.util.List;

public record PaymentCreateCommand(
	String userCode,
	String orderCode,
	Long totalAmount,
	List<String> productCodes
) {
}
