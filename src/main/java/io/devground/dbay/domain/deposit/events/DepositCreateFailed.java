package io.devground.dbay.domain.deposit.events;

public record DepositCreateFailed(
	String userCode,
	String msg
) {
}
