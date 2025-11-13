package io.devground.core.commands.order;

public record NotifyOrderCreateFailedAlertCommand(
	String userCode,
	String orderCode,
	String reason
) {
}
