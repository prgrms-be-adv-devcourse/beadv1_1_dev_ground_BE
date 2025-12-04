package io.devground.dbay.ddddeposit.application.port.out;

import io.devground.dbay.ddddeposit.domain.deposit.Deposit;

public interface DepositCommandPort {

	Deposit saveDeposit(Deposit deposit);

	void removeDeposit(String code);

}
