package io.devground.core.event.order.Temp.command;

public record CancelCreatePaymentCommand(
	String userCode,
	String orderCode,
	String msg
) {
}
