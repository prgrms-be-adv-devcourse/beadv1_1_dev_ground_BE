package io.devground.dbay.domain.deposit.mapper;

import io.devground.dbay.domain.deposit.dto.response.DepositBalanceResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositHistoryResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositResponse;
import io.devground.dbay.domain.deposit.entity.Deposit;
import io.devground.dbay.domain.deposit.entity.DepositHistory;

public class DepositMapper {

	public static DepositResponse toDepositResponse(Deposit deposit) {
		return new DepositResponse(
			deposit.getId(),
			deposit.getUserCode(),
			deposit.getCode(),
			deposit.getBalance(),
			deposit.getCreatedAt(),
			deposit.getUpdatedAt()
		);
	}

	public static DepositBalanceResponse toDepositBalanceResponse(Deposit deposit) {
		return new DepositBalanceResponse(
			deposit.getBalance()
		);
	}

	public static DepositHistoryResponse toDepositHistoryResponse(DepositHistory history) {
		return new DepositHistoryResponse(
			history.getId(),
			history.getDeposit() != null ? history.getDeposit().getId() : null,
			history.getUserCode(),
			history.getPayerDeposit() != null ? history.getPayerDeposit().getId() : null,
			history.getPayeeDeposit() != null ? history.getPayeeDeposit().getId() : null,
			history.getAmount(),
			history.getBalanceAfter(),
			history.getType(),
			history.getDescription(),
			history.getCreatedAt()
		);
	}
}
