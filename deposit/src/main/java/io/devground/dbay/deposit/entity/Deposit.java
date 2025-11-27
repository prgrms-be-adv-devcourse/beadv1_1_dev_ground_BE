package io.devground.dbay.deposit.entity;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Deposit extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String userCode;

	@Column(nullable = false)
	private Long balance = 0L;

	@Builder
	public Deposit(String userCode) {
		this.userCode = userCode;
	}

	public void charge(Long amount) {
		if (amount <= 0)
			throw new ServiceException(ErrorCode.AMOUNT_MUST_BE_POSITIVE);

		this.balance += amount;
	}

	public void withdraw(Long amount) {
		if (amount <= 0)
			throw new ServiceException(ErrorCode.AMOUNT_MUST_BE_POSITIVE);
		if (this.balance < amount)
			throw new ServiceException(ErrorCode.INSUFFICIENT_BALANCE);

		this.balance -= amount;
	}

}
