package io.devground.dbay.ddddeposit.domain.deposit;

import java.time.LocalDateTime;
import java.util.UUID;

import io.devground.dbay.ddddeposit.domain.exception.DomainException;
import io.devground.dbay.ddddeposit.domain.exception.vo.DomainErrorCode;

public class Deposit {

	private String code;

	private String userCode;

	private Long balance;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public Deposit(String userCode) {
		this.code = generateCode();
		this.userCode = userCode;
		this.balance = 0L;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Deposit(String code, String userCode, Long balance) {
		this.code = code;
		this.userCode = userCode;
		this.balance = balance;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	private String generateCode() {
		return UUID.randomUUID().toString();
	}

	public void charge(Long amount) {
		if (amount == null || amount <= 0) {
			throw new DomainException(DomainErrorCode.AMOUNT_MUST_BE_POSITIVE);
		}
		this.balance += amount;
		this.updatedAt = LocalDateTime.now();
	}

	public void withdraw(Long amount) {
		if (amount == null || amount <= 0) {
			throw new DomainException(DomainErrorCode.AMOUNT_MUST_BE_POSITIVE);
		}
		if (this.balance < amount) {
			throw new DomainException(DomainErrorCode.INSUFFICIENT_BALANCE);
		}
		this.balance -= amount;
		this.updatedAt = LocalDateTime.now();
	}

	public String getCode() {
		return code;
	}

	public String getUserCode() {
		return userCode;
	}

	public Long getBalance() {
		return balance;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
