package io.devground.core.event.order.Temp.command;

public record CompletePaymentCommand(
	String orderCode
) {
}
