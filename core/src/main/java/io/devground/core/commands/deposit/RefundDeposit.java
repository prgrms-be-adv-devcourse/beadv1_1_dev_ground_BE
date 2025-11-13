package io.devground.core.commands.deposit;

public record RefundDeposit(
	String userCode,
	Long amount,
	String type
) {
}
