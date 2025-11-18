package io.devground.core.event.deposit;

import java.util.List;

public record DepositWithdrawnSuccess(
	String userCode,
	String depositHistoryCode,
	Long amount,
	Long balanceAfter,
	String orderCode,
	List<String> productCodes
) {
}
