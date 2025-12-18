package io.devground.payments.deposit.application.port.out;

import io.devground.payments.deposit.domain.deposit.Deposit;

public interface DepositCommandPort {

	Deposit saveDeposit(Deposit deposit);

	void removeDeposit(String code);

}
