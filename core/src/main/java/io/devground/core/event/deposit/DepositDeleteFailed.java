package io.devground.core.event.deposit;

public record DepositDeleteFailed(
	String userCode,
	String msg
) {
}
