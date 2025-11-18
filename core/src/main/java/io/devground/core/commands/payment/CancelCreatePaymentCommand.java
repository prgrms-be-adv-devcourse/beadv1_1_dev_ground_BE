package io.devground.core.commands.payment;

public record CancelCreatePaymentCommand(
	String userCode,
	String orderCode,
	String msg
) {
}
