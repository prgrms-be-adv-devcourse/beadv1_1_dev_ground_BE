package io.devground.dbay.ddddeposit.domain.port.in;

import io.devground.dbay.ddddeposit.domain.deposit.Deposit;

/**
 * 조회 전용 UseCase (REST API용)
 * - 명령(Command)은 Kafka로 처리하므로 조회만 포함
 */
public interface DepositUseCase {

	Deposit getByUserCode(String userCode);

	Deposit getByCode(String code);

	boolean hasEnoughBalance(String depositCode, Long amount);

	boolean existsByUserCode(String userCode);
}
