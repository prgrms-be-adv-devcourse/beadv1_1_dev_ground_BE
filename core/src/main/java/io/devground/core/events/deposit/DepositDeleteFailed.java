package io.devground.core.events.deposit;

public record DepositDeleteFailed(
	String userCode,
	String msg
) {
}
