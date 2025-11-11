package io.devground.core.events.deposit;

public record DepositCreateFailed(
	String userCode,
	String msg
) {
}
