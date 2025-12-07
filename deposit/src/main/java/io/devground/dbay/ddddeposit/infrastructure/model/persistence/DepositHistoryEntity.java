package io.devground.dbay.ddddeposit.infrastructure.model.persistence;

import io.devground.core.model.entity.BaseEntity;
import io.devground.dbay.ddddeposit.domain.depositHistory.DepositHistory;
import io.devground.dbay.ddddeposit.domain.depositHistory.DepositHistoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositHistoryEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String code;

	@Column(nullable = false)
	private String userCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "depositId")
	private DepositEntity depositEntity;

	@Column(nullable = false)
	private String depositCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payerDepositId")
	private DepositEntity payerDepositEntity;

	@Column
	private String payerDepositCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payeeDepositId")
	private DepositEntity payeeDepositEntity;

	@Column
	private String payeeDepositCode;

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false)
	private Long balanceAfter;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DepositHistoryType type;

	@Column
	private String description;

	/**
	 * 정적 팩토리 메서드 - Mapper에서 사용
	 */
	public static DepositHistoryEntity of(DepositHistory depositHistory, DepositEntity depositEntity,
		DepositEntity payerDepositEntity, DepositEntity payeeDepositEntity) {
		DepositHistoryEntity entity = new DepositHistoryEntity();
		entity.code = depositHistory.getCode();
		entity.userCode = depositHistory.getUserCode();
		entity.depositEntity = depositEntity;
		entity.depositCode = depositHistory.getDepositCode();
		entity.payerDepositEntity = payerDepositEntity;
		entity.payerDepositCode = depositHistory.getPayerDepositCode();
		entity.payeeDepositEntity = payeeDepositEntity;
		entity.payeeDepositCode = depositHistory.getPayeeDepositCode();
		entity.amount = depositHistory.getAmount();
		entity.balanceAfter = depositHistory.getBalanceAfter();
		entity.type = depositHistory.getType();
		entity.description = depositHistory.getDescription();

		return entity;
	}
}
