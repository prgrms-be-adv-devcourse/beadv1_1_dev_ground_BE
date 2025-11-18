package io.devground.core.event.deposit;

public record DepositDeletedSuccess(
	String userCode,
	String msg
) {
}
