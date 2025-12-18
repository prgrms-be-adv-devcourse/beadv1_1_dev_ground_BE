package io.devground.core.commands.order;

public record CompleteOrderCommand(
	String userCode,
	String orderCode
) {
}
