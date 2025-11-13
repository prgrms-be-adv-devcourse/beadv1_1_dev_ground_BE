package io.devground.core.commands.deposit;

public record ChargeDeposit(
	String userCode,
	Long amount,
	String type
) {
}
