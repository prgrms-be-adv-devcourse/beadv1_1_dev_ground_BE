package io.devground.core.commands.user;

public record NotifyDepositDeleteFailedAlertCommand(
	String userCode,
	String msg
) {
}
