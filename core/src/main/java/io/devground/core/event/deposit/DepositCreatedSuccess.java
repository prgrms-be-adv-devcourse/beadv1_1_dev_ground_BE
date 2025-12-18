package io.devground.core.event.deposit;

public record DepositCreatedSuccess(
	String userCode,
	String depositCode
) {
}
