package io.devground.dbay.ddddeposit.application.port.out;

import java.util.Optional;

import io.devground.dbay.ddddeposit.domain.deposit.Deposit;

public interface DepositPersistencePort {

	Optional<Deposit> getDeposit(String code);

	Optional<Deposit> getDepositByUserCode(String userCode);
}
