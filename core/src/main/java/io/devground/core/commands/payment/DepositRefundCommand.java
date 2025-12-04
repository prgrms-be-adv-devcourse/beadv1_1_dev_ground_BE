package io.devground.core.commands.payment;

public record DepositRefundCommand(
	String userCode,
	Long amount,
	String orderCode
) {
}
