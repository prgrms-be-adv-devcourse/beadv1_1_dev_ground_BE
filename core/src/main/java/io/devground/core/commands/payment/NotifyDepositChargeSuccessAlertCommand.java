package io.devground.core.commands.payment;

public record NotifyDepositChargeSuccessAlertCommand(
	String userCode,
	Long amount
) {
}
