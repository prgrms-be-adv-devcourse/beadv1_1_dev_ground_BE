package io.devground.payments.deposit.application.port.out;

import java.util.Optional;

import io.devground.payments.deposit.domain.deposit.Deposit;

public interface DepositPersistencePort {

	Optional<Deposit> getDeposit(String code);

	Optional<Deposit> getDepositByUserCode(String userCode);
}
