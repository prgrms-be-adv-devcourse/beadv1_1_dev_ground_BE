package io.devground.dbay.ddddeposit.infrastructure.model.persistence;

import io.devground.core.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String userCode;

	@Column(nullable = false)
	private Long balance;

	public static DepositEntity of(String code, String userCode) {
		DepositEntity depositEntity = new DepositEntity();
		depositEntity.register(code);
		depositEntity.userCode = userCode;
		depositEntity.balance = 0L;

		return depositEntity;
	}
}
