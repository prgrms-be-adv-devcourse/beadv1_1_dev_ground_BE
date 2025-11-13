package io.devground.core.events.deposit;

public record DepositDeletedSuccess(
	String userCode,
	String msg
) {
}
