package io.devground.dbay.ddddeposit.domain.port.in;

import io.devground.dbay.ddddeposit.domain.deposit.Deposit;
import io.devground.dbay.ddddeposit.domain.depositHistory.DepositHistory;
import io.devground.dbay.ddddeposit.domain.depositHistory.DepositHistoryType;

public interface DepositEventUseCase {

	Deposit createDeposit(String userCode);

	DepositHistory charge(String userCode, DepositHistoryType type, Long amount);

	DepositHistory withdraw(String userCode, DepositHistoryType type, Long amount);

	DepositHistory refund(String userCode, DepositHistoryType type, Long amount);

	void deleteDeposit(String userCode);
}
