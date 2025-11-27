package io.devground.dbay.deposit.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.dto.deposit.response.DepositBalanceResponse;
import io.devground.core.dto.deposit.response.DepositHistoryResponse;
import io.devground.core.dto.deposit.response.DepositResponse;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;

import io.devground.dbay.deposit.entity.Deposit;
import io.devground.dbay.deposit.entity.DepositHistory;
import io.devground.dbay.deposit.entity.vo.DepositHistoryType;
import io.devground.dbay.deposit.mapper.DepositMapper;
import io.devground.dbay.deposit.repository.DepositHistoryJpaRepository;
import io.devground.dbay.deposit.repository.DepositJpaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepositServiceImpl implements DepositService {

	private final DepositJpaRepository depositRepository;
	private final DepositHistoryJpaRepository depositHistoryRepository;

	@Override
	@Transactional
	public DepositResponse createDeposit(String userCode) {
		if (depositRepository.existsByUserCode(userCode)) {
			throw new ServiceException(ErrorCode.DEPOSIT_ALREADY_EXISTS);
		}

		Deposit deposit = Deposit.builder()
			.userCode(userCode)
			.build();

		Deposit save = depositRepository.save(deposit);

		return DepositMapper.toDepositResponse(save);
	}

	@Override
	public DepositResponse getByUserCode(String userCode) {
		Deposit deposit = getDepositByUserCode(userCode);

		return DepositMapper.toDepositResponse(deposit);
	}

	@Override
	public DepositBalanceResponse getByBalance(String userCode) {
		Deposit deposit = getDepositByUserCode(userCode);

		return DepositMapper.toDepositBalanceResponse(deposit);
	}

	@Override
	public boolean hasEnoughBalance(String depositCode, Long amount) {
		Deposit deposit = depositRepository.findByCode(depositCode)
			.orElseThrow(() -> new ServiceException(ErrorCode.DEPOSIT_NOT_FOUND));

		return deposit.getBalance() >= amount;
	}

	@Override
	@Transactional
	public DepositHistoryResponse charge(String userCode, DepositHistoryType type, Long amount) {
		DepositHistory depositHistory = applyHistory(userCode, type, amount);

		return DepositMapper.toDepositHistoryResponse(depositHistory);
	}

	@Override
	@Transactional
	public DepositHistoryResponse withdraw(String userCode, DepositHistoryType type, Long amount) {
		DepositHistory depositHistory = applyHistory(userCode, type, amount);

		return DepositMapper.toDepositHistoryResponse(depositHistory);
	}

	@Override
	@Transactional
	public DepositHistoryResponse refund(String userCode, DepositHistoryType type, Long amount) {
		DepositHistory depositHistory = applyHistory(userCode, type, amount);

		return DepositMapper.toDepositHistoryResponse(depositHistory);
	}

	@Override
	public Page<DepositHistory> getDepositHistories(String depositCode, Pageable pageable) {
		return depositHistoryRepository.findByUserCode(depositCode, pageable);
	}

	@Override
	public DepositHistoryResponse getDepositHistoryByCode(String historyCode) {
		DepositHistory depositHistory = depositHistoryRepository.findByCode(historyCode)
			.orElseThrow(() -> new ServiceException(ErrorCode.DEPOSIT_HISTORY_NOT_FOUND));

		return DepositMapper.toDepositHistoryResponse(depositHistory);
	}

	@Override
	@Transactional
	public void deleteDeposit(String userCode) {
		Deposit deposit = getDepositByUserCode(userCode);

		deposit.delete();
	}

	private Deposit getDepositByUserCode(String userCode) {
		return depositRepository.findByUserCode(userCode)
			.orElseThrow(() -> new ServiceException(ErrorCode.DEPOSIT_NOT_FOUND));
	}

	private DepositHistory applyHistory(String userCode, DepositHistoryType type, Long amount) {
		Deposit deposit = getDepositByUserCode(userCode);

		String description = generateDescription(type, amount);

		DepositHistory history = DepositHistory.builder()
			.type(type)
			.deposit(deposit)
			.userCode(userCode)
			.amount(amount)
			.description(description)
			.build();

		history = depositHistoryRepository.save(history);

		return history;
	}

	private String generateDescription(DepositHistoryType type, Long amount) {
		return switch (type) {
			case CHARGE_TRANSFER -> String.format("계좌 이체 충전 %,d원", amount);
			case CHARGE_TOSS -> String.format("토스 결제 충전 %,d원", amount);
			case PAYMENT_TOSS -> String.format("토스 결제 %,d원", amount);
			case PAYMENT_INTERNAL -> String.format("예치금 결제 %,d원", amount);
			case REFUND_INTERNAL -> String.format("예치금 환불 %,d원", amount);
			case REFUND_TOSS -> String.format("토스 환불 %,d원", amount);
			case SETTLEMENT -> String.format("정산 입금 %,d원", amount);
		};
	}
}
