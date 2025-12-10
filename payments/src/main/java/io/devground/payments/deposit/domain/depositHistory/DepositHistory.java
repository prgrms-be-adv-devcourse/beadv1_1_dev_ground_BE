package io.devground.payments.deposit.domain.depositHistory;

import java.time.LocalDateTime;
import java.util.UUID;

import io.devground.payments.deposit.domain.deposit.Deposit;

public class DepositHistory {

	private String code;

	private String userCode;

	private String depositCode;

	private String payerDepositCode;

	private String payeeDepositCode;

	private Long amount;

	private Long balanceAfter;

	private DepositHistoryType type;

	private String description;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public DepositHistory(String userCode, Deposit deposit, Deposit payerDeposit, Deposit payeeDeposit,
		Long amount, DepositHistoryType type, String description) {
		this.code = generateCode();
		this.userCode = userCode;
		this.depositCode = deposit.getCode();
		this.payerDepositCode = payerDeposit.getCode();
		this.payeeDepositCode = payeeDeposit.getCode();
		this.amount = amount;
		this.type = type;
		this.description = description;

		// 타입에 따라 deposit에 금액 적용
		type.apply(deposit, amount);
		this.balanceAfter = deposit.getBalance();

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * DB에서 조회한 데이터를 복원하는 생성자 (Mapper 전용)
	 */
	public DepositHistory(String code, String userCode, String depositCode, String payerDepositCode,
		String payeeDepositCode, Long amount, Long balanceAfter, DepositHistoryType type,
		String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.code = code;
		this.userCode = userCode;
		this.depositCode = depositCode;
		this.payerDepositCode = payerDepositCode;
		this.payeeDepositCode = payeeDepositCode;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.type = type;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	private String generateCode() {
		return UUID.randomUUID().toString();
	}

	public static DepositHistoryBuilder builder() {
		return new DepositHistoryBuilder();
	}

	public static class DepositHistoryBuilder {
		private String userCode;
		private Deposit deposit;
		private Deposit payerDeposit;
		private Deposit payeeDeposit;
		private Long amount;
		private DepositHistoryType type;
		private String description;

		public DepositHistoryBuilder userCode(String userCode) {
			this.userCode = userCode;
			return this;
		}

		public DepositHistoryBuilder deposit(Deposit deposit) {
			this.deposit = deposit;
			return this;
		}

		public DepositHistoryBuilder payerDeposit(Deposit payerDeposit) {
			this.payerDeposit = payerDeposit;
			return this;
		}

		public DepositHistoryBuilder payeeDeposit(Deposit payeeDeposit) {
			this.payeeDeposit = payeeDeposit;
			return this;
		}

		public DepositHistoryBuilder amount(Long amount) {
			this.amount = amount;
			return this;
		}

		public DepositHistoryBuilder type(DepositHistoryType type) {
			this.type = type;
			return this;
		}

		public DepositHistoryBuilder description(String description) {
			this.description = description;
			return this;
		}

		public DepositHistory build() {
			return new DepositHistory(userCode, deposit, payerDeposit, payeeDeposit, amount, type, description);
		}
	}

	public String getCode() {
		return code;
	}

	public String getUserCode() {
		return userCode;
	}

	public String getDepositCode() {
		return depositCode;
	}

	public String getPayerDepositCode() {
		return payerDepositCode;
	}

	public String getPayeeDepositCode() {
		return payeeDepositCode;
	}

	public Long getAmount() {
		return amount;
	}

	public Long getBalanceAfter() {
		return balanceAfter;
	}

	public DepositHistoryType getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

}
