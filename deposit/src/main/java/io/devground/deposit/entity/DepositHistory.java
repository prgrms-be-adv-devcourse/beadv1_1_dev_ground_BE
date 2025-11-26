package io.devground.deposit.entity;

import io.devground.core.model.entity.BaseEntity;
import io.devground.deposit.entity.vo.DepositHistoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "depositId")
	private Deposit deposit;

	@Column(nullable = false)
	private String userCode;

	@ManyToOne()
	@JoinColumn(name = "payerDepositId")
	private Deposit payerDeposit;

	@ManyToOne
	@JoinColumn(name = "payeeDepositId")
	private Deposit payeeDeposit;

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false)
	private Long balanceAfter;

	@Enumerated(EnumType.STRING)
	private DepositHistoryType type;

	@Column(nullable = true)
	private String description;

	@Builder
	public DepositHistory(String userCode, Deposit deposit, Deposit payerDeposit, Deposit payeeDeposit, Long amount,
		DepositHistoryType type,
		String description) {
		this.userCode = userCode;
		this.deposit = deposit;
		this.payerDeposit = payerDeposit;
		this.payeeDeposit = payeeDeposit;
		this.amount = amount;
		this.type = type;

		this.type.apply(deposit, amount);
		this.balanceAfter = deposit.getBalance();
		this.description = description;
	}
}
