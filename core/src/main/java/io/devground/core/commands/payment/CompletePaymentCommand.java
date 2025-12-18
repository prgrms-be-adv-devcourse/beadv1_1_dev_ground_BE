package io.devground.core.commands.payment;

public record CompletePaymentCommand(
	String orderCode
) {
}
