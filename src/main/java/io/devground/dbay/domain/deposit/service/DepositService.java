package io.devground.dbay.domain.deposit.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.devground.dbay.domain.deposit.dto.response.DepositBalanceResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositHistoryResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositResponse;
import io.devground.dbay.domain.deposit.entity.Deposit;
import io.devground.dbay.domain.deposit.entity.DepositHistory;
import io.devground.dbay.domain.deposit.entity.vo.DepositHistoryType;

public interface DepositService {

	DepositResponse createDeposit(String userCode);

	DepositResponse getByUserCode(String userCode);

	boolean hasEnoughBalance(String depositCode, Long amount);

	DepositBalanceResponse getByBalance(String userCode);

	DepositHistoryResponse charge(String userCode, DepositHistoryType type, Long amount);

	DepositHistoryResponse withdraw(String userCode, DepositHistoryType type, Long amount);

	DepositHistoryResponse refund(String userCode, DepositHistoryType type, Long amount);

	Page<DepositHistory> getDepositHistories(String userCode, Pageable pageable);

	DepositHistoryResponse getDepositHistoryByCode(String historyCode);

	void deleteDeposit(String userCode);

}
