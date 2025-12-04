package io.devground.dbay.ddddeposit.infrastructure.mapper;

import io.devground.core.dto.deposit.response.DepositBalanceResponse;
import io.devground.core.dto.deposit.response.DepositResponse;
import io.devground.dbay.ddddeposit.domain.deposit.Deposit;
import io.devground.dbay.ddddeposit.infrastructure.model.persistence.DepositEntity;

public class DepositMapper {

	public static Deposit toDomain(DepositEntity depositEntity) {
		return new Deposit(
			depositEntity.getCode(),
			depositEntity.getCode(),
			depositEntity.getBalance()
		);
	}

	public static DepositResponse toDepositResponse(Deposit deposit) {
		return DepositResponse.builder()
			.depositCode(deposit.getCode())
			.balance(deposit.getBalance())
			.userCode(deposit.getUserCode())
			.createdAt(deposit.getCreatedAt())
			.updatedAt(deposit.getUpdatedAt())
			.build();
	}

	public static DepositBalanceResponse toDepositBalanceResponse(Deposit deposit) {
		return DepositBalanceResponse.builder()
			.userCode(deposit.getUserCode())
			.balance(deposit.getBalance())
			.build();
	}
}
