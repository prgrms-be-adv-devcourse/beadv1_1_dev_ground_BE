package io.devground.core.commands.user;

public record NotifyCartDeleteFailedAlertCommand(
	String userCode,
	String msg
) {
}
