package io.devground.core.event.deposit;

public record DepositCreateFailed(
	String userCode,
	String msg
) {
}
