package io.devground.dbay.domain.deposit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.deposit.dto.response.DepositBalanceResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositHistoryResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositResponse;
import io.devground.dbay.domain.deposit.entity.Deposit;
import io.devground.dbay.domain.deposit.entity.DepositHistory;
import io.devground.dbay.domain.deposit.entity.vo.DepositHistoryType;
import io.devground.dbay.domain.deposit.mapper.DepositMapper;
import io.devground.dbay.domain.deposit.repository.DepositHistoryJpaRepository;
import io.devground.dbay.domain.deposit.repository.DepositJpaRepository;

@DisplayName("비즈니스 로직 - 예치금")
@ExtendWith(MockitoExtension.class)
class DepositServiceImplTest {

	@InjectMocks
	private DepositServiceImpl depositService;

	@Mock
	private DepositJpaRepository depositRepository;

	@Mock
	private DepositHistoryJpaRepository depositHistoryRepository;

	@DisplayName("사용자 코드를 입력하면, 예치금을 생성한다.")
	@Test
	void givenUserCode_whenCreatingDeposit_thenReturnsDeposit() {
		// given
		String userCode = "USER_CODE";
		Deposit expected = Deposit.builder().userCode(userCode).build();

		given(depositRepository.existsByUserCode(userCode))
			.willReturn(false);
		given(depositRepository.save(any(Deposit.class)))
			.willReturn(expected);

		// when
		DepositResponse actual = depositService.createDeposit(userCode);

		// then
		assertThat(actual).hasFieldOrPropertyWithValue("userCode", expected.getUserCode());
		then(depositRepository).should().existsByUserCode(userCode);
		then(depositRepository).should().save(any(Deposit.class));
	}

	@DisplayName("이미 예치금이 존재하는 사용자 코드로 생성하면, 예외를 발생시킨다.")
	@Test
	void givenExistingUserCode_whenCreatingDeposit_thenThrowsException() {
		// given
		String userCode = "EXISTING_USER_CODE";

		given(depositRepository.existsByUserCode(userCode))
			.willReturn(true);

		// when & then
		assertThatThrownBy(() -> depositService.createDeposit(userCode))
			.isInstanceOf(ServiceException.class)
			.hasMessage("409 : 이미 예금 계정이 존재합니다.");

		then(depositRepository).should().existsByUserCode(userCode);
		then(depositRepository).should(never()).save(any(Deposit.class));
	}

	@DisplayName("사용자 코드를 입력하면, 예치금을 조회하여 반환한다.")
	@Test
	void givenUserCode_whenSearchingDeposit_thenReturnsDepositResponse() {
		// given
		String userCode = "USER_CODE";
		Deposit expected = Deposit.builder()
			.userCode(userCode)
			.build();

		expected.charge(10000L);

		given(depositRepository.existsByUserCode(userCode))
			.willReturn(true);
		given(depositRepository.findByUserCode(userCode))
			.willReturn(Optional.of(expected));

		// when
		DepositResponse actual = depositService.getByUserCode(userCode);

		// then
		assertThat(actual)
			.hasFieldOrPropertyWithValue("userCode", expected.getUserCode())
			.hasFieldOrPropertyWithValue("balance", 10000L);
		then(depositRepository).should().existsByUserCode(userCode);
		then(depositRepository).should().findByUserCode(userCode);
	}

	@DisplayName("존재하지 않는 사용자 코드로 조회하면, 예외를 발생시킨다.")
	@Test
	void givenNonExistentUserCode_whenSearchingDeposit_thenThrowsException() {
		// given
		String userCode = "NON_EXIST_USER_CODE";

		// when & then
		assertThatThrownBy(() -> depositService.getByUserCode(userCode))
			.isInstanceOf(ServiceException.class)
			.hasMessage("404 : 예금 계정을 찾을 수 없습니다.");

		then(depositRepository).should(never()).findByUserCode(anyString());
	}

	@DisplayName("사용자 코드를 입력하면, 잔액 정보를 반환한다.")
	@Test
	void givenUserCode_whenSearchingBalance_thenReturnsBalanceResponse() {
		// given
		String userCode = "USER_CODE";
		Deposit deposit = Deposit.builder()
			.userCode(userCode)
			.build();
		deposit.charge(50000L);

		given(depositRepository.findByUserCode(userCode))
			.willReturn(Optional.of(deposit));

		// when
		DepositBalanceResponse actual = depositService.getByBalance(userCode);

		// then
		assertThat(actual)
			.hasFieldOrPropertyWithValue("balance", 50000L);
		then(depositRepository).should().findByUserCode(userCode);
	}

	@DisplayName("예치금 코드와 금액을 입력하면, 잔액이 충분한 경우 true를 반환한다.")
	@Test
	void givenDepositCodeAndAmount_whenCheckingBalance_thenReturnsTrue() {
		// given
		String depositCode = "DEPOSIT_CODE";
		Long amount = 30000L;
		Deposit deposit = Deposit.builder()
			.userCode("USER_CODE")
			.build();
		deposit.charge(50000L); // 잔액 50,000원

		given(depositRepository.findByCode(depositCode))
			.willReturn(Optional.of(deposit));

		// when
		boolean actual = depositService.hasEnoughBalance(depositCode, amount);

		// then
		assertThat(actual).isTrue();
		then(depositRepository).should().findByCode(depositCode);
	}

	@DisplayName("예치금 코드와 금액을 입력하면, 잔액이 부족한 경우 false를 반환한다.")
	@Test
	void givenDepositCodeAndAmount_whenCheckingBalance_thenReturnsFalse() {
		// given
		String depositCode = "VALID_CODE";
		Long amount = 100000L;
		Deposit deposit = Deposit.builder()
			.userCode("USER_CODE")
			.build();
		deposit.charge(50000L); // 잔액 50,000원

		given(depositRepository.findByCode(depositCode))
			.willReturn(Optional.of(deposit));

		// when
		boolean actual = depositService.hasEnoughBalance(depositCode, amount);

		// then
		assertThat(actual).isFalse();
		then(depositRepository).should().findByCode(depositCode);
	}

	@DisplayName("존재하지 않는 예치금 코드로 잔액을 확인하면, 예외를 발생시킨다.")
	@Test
	void givenInvalidDepositCode_whenCheckingBalance_thenThrowsException() {
		// given
		String depositCode = "INVALID_CODE";
		Long amount = 10000L;

		given(depositRepository.findByCode(depositCode))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> depositService.hasEnoughBalance(depositCode, amount))
			.isInstanceOf(ServiceException.class)
			.hasMessage("404 : 예금 계정을 찾을 수 없습니다.");

		then(depositRepository).should().findByCode(depositCode);
	}

	@DisplayName("사용자 코드와 금액을 입력하면, 충전 후 거래 내역을 반환한다.")
	@Test
	void givenUserCodeAndAmount_whenCharging_thenReturnsHistoryResponse() {
		// given
		String userCode = "USER_CODE";
		Long amount = 10000L;
		Deposit deposit = Deposit.builder()
			.userCode(userCode)
			.build();

		DepositHistory expected = DepositHistory.builder()
			.userCode(userCode)
			.deposit(deposit)
			.amount(amount)
			.type(DepositHistoryType.CHARGE_TOSS)
			.build();

		given(depositRepository.findByUserCode(userCode))
			.willReturn(Optional.of(deposit));
		given(depositHistoryRepository.save(any(DepositHistory.class)))
			.willReturn(expected);

		// when
		depositService.charge(userCode, DepositHistoryType.CHARGE_TOSS, amount);

		// then
		then(depositRepository).should().findByUserCode(userCode);
		then(depositHistoryRepository).should().save(any(DepositHistory.class));
	}

	@DisplayName("존재하지 않는 사용자 코드로 충전하면, 예외를 발생시킨다.")
	@Test
	void givenNonExistentUserCode_whenCharging_thenThrowsException() {
		// given
		String userCode = "NON_EXIST_USER_CODE";
		Long amount = 10000L;

		given(depositRepository.findByUserCode(userCode))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> depositService.charge(userCode, DepositHistoryType.CHARGE_TOSS, amount))
			.isInstanceOf(ServiceException.class)
			.hasMessage("404 : 예금 계정을 찾을 수 없습니다.");

		then(depositRepository).should().findByUserCode(userCode);
		then(depositHistoryRepository).should(never()).save(any(DepositHistory.class));
	}

	@DisplayName("존재하지 않는 사용자 코드로 인출하면, 예외를 발생시킨다.")
	@Test
	void givenNonExistentUserCode_whenWithdrawing_thenThrowsException() {
		// given
		String userCode = "NON_EXIST_USER_CODE";
		Long amount = 5000L;

		given(depositRepository.findByUserCode(userCode))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> depositService.withdraw(userCode, DepositHistoryType.PAYMENT_TOSS, amount))
			.isInstanceOf(ServiceException.class)
			.hasMessage("404 : 예금 계정을 찾을 수 없습니다.");

		then(depositRepository).should().findByUserCode(userCode);
		then(depositHistoryRepository).should(never()).save(any(DepositHistory.class));
	}

	@DisplayName("사용자 코드와 금액을 입력하면, 환불 후 거래 내역을 반환한다.")
	@Test
	void givenUserCodeAndAmount_whenRefunding_thenReturnsHistoryResponse() {
		// given
		String userCode = "USER_CODE";
		Long amount = 3000L;
		Deposit deposit = Deposit.builder()
			.userCode(userCode)
			.build();

		DepositHistory expected = DepositHistory.builder()
			.userCode(userCode)
			.deposit(deposit)
			.amount(amount)
			.type(DepositHistoryType.REFUND_TOSS)
			.build();

		given(depositRepository.findByUserCode(userCode))
			.willReturn(Optional.of(deposit));
		given(depositHistoryRepository.save(any(DepositHistory.class)))
			.willReturn(expected);

		// when
		depositService.refund(userCode, DepositHistoryType.REFUND_TOSS, amount);

		// then
		then(depositRepository).should().findByUserCode(userCode);
		then(depositHistoryRepository).should().save(any(DepositHistory.class));
	}

	@DisplayName("존재하지 않는 사용자 코드로 환불하면, 예외를 발생시킨다.")
	@Test
	void givenNonExistentUserCode_whenRefunding_thenThrowsException() {
		// given
		String userCode = "NON_EXIST_USER_CODE";
		Long amount = 3000L;

		given(depositRepository.findByUserCode(userCode))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> depositService.refund(userCode, DepositHistoryType.REFUND_TOSS, amount))
			.isInstanceOf(ServiceException.class)
			.hasMessage("404 : 예금 계정을 찾을 수 없습니다.");

		then(depositRepository).should().findByUserCode(userCode);
		then(depositHistoryRepository).should(never()).save(any(DepositHistory.class));
	}

}
