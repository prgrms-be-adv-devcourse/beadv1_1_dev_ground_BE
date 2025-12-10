package io.devground.payments.deposit.application.service;

import org.springframework.stereotype.Service;

import io.devground.payments.deposit.application.exception.ServiceException;
import io.devground.payments.deposit.application.exception.vo.ServiceErrorCode;
import io.devground.payments.deposit.application.port.out.DepositHistoryCommandPort;
import io.devground.payments.deposit.application.port.out.DepositPersistencePort;
import io.devground.payments.deposit.domain.deposit.Deposit;
import io.devground.payments.deposit.domain.depositHistory.DepositHistory;
import io.devground.payments.deposit.domain.pagination.PageDto;
import io.devground.payments.deposit.domain.pagination.PageQuery;
import io.devground.payments.deposit.domain.port.in.DepositHistoryUseCase;
import io.devground.payments.deposit.domain.port.in.DepositUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositApplication implements DepositUseCase, DepositHistoryUseCase {

	private final DepositPersistencePort depositPersistencePort;
	private final DepositHistoryCommandPort depositHistoryPersistencePort;

	@Override
	public Deposit getByUserCode(String userCode) {
		return depositPersistencePort.getDepositByUserCode(userCode)
			.orElseThrow(() -> new ServiceException(ServiceErrorCode.DEPOSIT_NOT_FOUND));
	}

	@Override
	public Deposit getByCode(String code) {
		return depositPersistencePort.getDeposit(code)
			.orElseThrow(() -> new ServiceException(ServiceErrorCode.DEPOSIT_NOT_FOUND));
	}

	@Override
	public boolean hasEnoughBalance(String depositCode, Long amount) {
		Deposit deposit = depositPersistencePort.getDeposit(depositCode)
			.orElseThrow(() -> new ServiceException(ServiceErrorCode.DEPOSIT_NOT_FOUND));

		return deposit.getBalance() >= amount;
	}

	@Override
	public PageDto<DepositHistory> getDepositHistories(String depositCode, PageQuery pageQuery) {
		return depositHistoryPersistencePort.getDepositHistories(depositCode, pageQuery);
	}

	@Override
	public DepositHistory getDepositHistoryByCode(String historyCode) {
		return depositHistoryPersistencePort.getDepositHistoryByCode(historyCode)
			.orElseThrow(() -> new ServiceException(ServiceErrorCode.DEPOSIT_HISTORY_NOT_FOUND));
	}

	@Override
	public boolean existsByUserCode(String userCode) {
		return depositPersistencePort.getDepositByUserCode(userCode).isPresent();
	}

}
