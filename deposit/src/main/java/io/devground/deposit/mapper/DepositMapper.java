package io.devground.deposit.mapper;

import io.devground.core.dto.deposit.response.DepositBalanceResponse;
import io.devground.core.dto.deposit.response.DepositHistoryResponse;
import io.devground.core.dto.deposit.response.DepositResponse;
import io.devground.deposit.entity.Deposit;
import io.devground.deposit.entity.DepositHistory;

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
			history.getCode(),
			history.getDeposit() != null ? history.getDeposit().getId() : null,
			history.getUserCode(),
			history.getPayerDeposit() != null ? history.getPayerDeposit().getId() : null,
			history.getPayeeDeposit() != null ? history.getPayeeDeposit().getId() : null,
			history.getAmount(),
			history.getBalanceAfter(),
			convertToCoreDepositHistoryType(history.getType()),
			history.getDescription(),
			history.getCreatedAt()
		);
	}

	private static io.devground.core.model.vo.DepositHistoryType convertToCoreDepositHistoryType(
		io.devground.deposit.entity.vo.DepositHistoryType entityType) {
		if (entityType == null) {
			return null;
		}
		return io.devground.core.model.vo.DepositHistoryType.valueOf(entityType.name());
	}
}
