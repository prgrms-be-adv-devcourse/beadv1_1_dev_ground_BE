package io.devground.core.event.order.Temp.command;

public record PaymentCreateCommand(
	String userCode,
	String orderCode,
	Long totalAmount
) {
}
