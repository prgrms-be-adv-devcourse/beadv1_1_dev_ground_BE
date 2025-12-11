package io.devground.payments.deposit.infrastructure.mapper;

import io.devground.core.dto.deposit.response.DepositHistoryResponse;
import io.devground.core.model.vo.DepositHistoryType;
import io.devground.payments.deposit.domain.depositHistory.DepositHistory;
import io.devground.payments.deposit.infrastructure.model.persistence.DepositEntity;
import io.devground.payments.deposit.infrastructure.model.persistence.DepositHistoryEntity;

public class DepositHistoryMapper {

	/**
	 * Domain -> Entity 변환
	 */
	public static DepositHistoryEntity toEntity(DepositHistory depositHistory, DepositEntity depositEntity,
		DepositEntity payerDepositEntity, DepositEntity payeeDepositEntity) {
		return DepositHistoryEntity.of(
			depositHistory,
			depositEntity,
			payerDepositEntity,
			payeeDepositEntity
		);
	}

	/**
	 * Entity -> Domain 변환 (재구성)
	 */
	public static DepositHistory toDomain(DepositHistoryEntity entity) {
		return new DepositHistory(
			entity.getCode(),
			entity.getUserCode(),
			entity.getDepositCode(),
			entity.getPayerDepositCode(),
			entity.getPayeeDepositCode(),
			entity.getAmount(),
			entity.getBalanceAfter(),
			entity.getType(),
			entity.getDescription(),
			entity.getCreatedAt(),
			entity.getUpdatedAt()
		);
	}

	/**
	 * Domain -> Response DTO 변환
	 */
	public static DepositHistoryResponse toDepositHistoryResponse(DepositHistory depositHistory) {
		return DepositHistoryResponse.builder()
			.code(depositHistory.getCode())
			.userCode(depositHistory.getUserCode())
			.depositCode(depositHistory.getDepositCode())
			.payerDepositCode(depositHistory.getPayerDepositCode())
			.payeeDepositCode(depositHistory.getPayeeDepositCode())
			.amount(depositHistory.getAmount())
			.balanceAfter(depositHistory.getBalanceAfter())
			.type(DepositHistoryType.valueOf(depositHistory.getType().name()))
			.description(depositHistory.getDescription())
			.createdAt(depositHistory.getCreatedAt())
			.build();
	}


}
