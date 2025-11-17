package io.devground.core.commands.payment;

public record NotifyDepositChargeFailedAlertCommand(
	String userCode,
	String msg
) {
}
