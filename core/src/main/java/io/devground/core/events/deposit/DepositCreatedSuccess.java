package io.devground.core.events.deposit;

public record DepositCreatedSuccess(
	String userCode,
	String depositCode
) {
}
