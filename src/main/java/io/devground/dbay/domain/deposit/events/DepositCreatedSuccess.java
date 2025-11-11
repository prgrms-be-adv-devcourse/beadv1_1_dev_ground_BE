package io.devground.dbay.domain.deposit.events;

public record DepositCreatedSuccess(
	String userCode,
	String depositCode
) {
}
