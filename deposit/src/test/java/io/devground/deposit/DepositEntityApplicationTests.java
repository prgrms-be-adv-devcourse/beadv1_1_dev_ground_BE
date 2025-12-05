package io.devground.deposit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.devground.dbay.ddddeposit.application.exception.ServiceException;
import io.devground.dbay.ddddeposit.application.port.out.DepositCommandPort;
import io.devground.dbay.ddddeposit.application.port.out.DepositHistoryCommandPort;
import io.devground.dbay.ddddeposit.application.port.out.DepositPersistencePort;
import io.devground.dbay.ddddeposit.application.service.DepositApplication;
import io.devground.dbay.ddddeposit.application.service.DepositEventApplication;
import io.devground.dbay.ddddeposit.domain.deposit.Deposit;
import io.devground.dbay.ddddeposit.domain.depositHistory.DepositHistory;
import io.devground.dbay.ddddeposit.domain.depositHistory.DepositHistoryType;

@DisplayName("비즈니스 로직 - 예치금 (DDD)")
@ExtendWith(MockitoExtension.class)
class DepositEntityApplicationTests {

	@InjectMocks
	private DepositApplication depositApplication;

	@InjectMocks
	private DepositEventApplication depositEventApplication;

	@Mock
	private DepositPersistencePort depositPersistencePort;

	@Mock
	private DepositCommandPort depositCommandPort;

	@Mock
	private DepositHistoryCommandPort depositHistoryCommandPort;

	@Nested
	@DisplayName("사용자 코드를 입력하면,")
	class describe_givenUserCode {

		@DisplayName("사용자 코드를 입력하면, 예치금을 생성한다.")
		@Test
		void givenUserCode_whenCreatingDeposit_thenReturnsDeposit() {
			// given
			String userCode = "USER_CODE";
			Deposit expected = new Deposit(userCode);

			given(depositCommandPort.saveDeposit(any(Deposit.class)))
				.willReturn(expected);

			// when
			Deposit actual = depositEventApplication.createDeposit(userCode);

			// then
			assertThat(actual).isNotNull();
			assertThat(actual.getUserCode()).isEqualTo(userCode);
			then(depositCommandPort).should().saveDeposit(any(Deposit.class));
		}

		@DisplayName("사용자 코드를 입력하면, 예치금을 조회하여 반환한다.")
		@Test
		void givenUserCode_whenSearchingDeposit_thenReturnsDeposit() {
			// given
			String userCode = "USER_CODE";
			Deposit expected = new Deposit(userCode);
			expected.charge(10000L);

			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.of(expected));

			// when
			Deposit actual = depositApplication.getByUserCode(userCode);

			// then
			assertThat(actual).isNotNull();
			assertThat(actual.getUserCode()).isEqualTo(userCode);
			assertThat(actual.getBalance()).isEqualTo(10000L);
			then(depositPersistencePort).should().getDepositByUserCode(userCode);
		}

		@DisplayName("사용자 코드가 존재하는지 확인한다.")
		@Test
		void givenUserCode_whenCheckingExistence_thenReturnsTrue() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);

			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.of(deposit));

			// when
			boolean actual = depositApplication.existsByUserCode(userCode);

			// then
			assertThat(actual).isTrue();
			then(depositPersistencePort).should().getDepositByUserCode(userCode);
		}

		@DisplayName("존재하지 않는 사용자 코드로 조회하면, 예외를 발생시킨다.")
		@Test
		void givenNonExistentUserCode_whenSearchingDeposit_thenThrowsException() {
			// given
			String userCode = "NON_EXIST_USER_CODE";

			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> depositApplication.getByUserCode(userCode))
				.isInstanceOf(ServiceException.class);

			then(depositPersistencePort).should().getDepositByUserCode(userCode);
		}
	}

	@Nested
	@DisplayName("예치금 코드를 입력하면,")
	class describe_givenDepositCode {

		@DisplayName("예치금 코드를 입력하면, 예치금을 조회하여 반환한다.")
		@Test
		void givenDepositCode_whenSearchingDeposit_thenReturnsDeposit() {
			// given
			String depositCode = "DEPOSIT_CODE";
			Deposit expected = new Deposit("USER_CODE");

			given(depositPersistencePort.getDeposit(depositCode))
				.willReturn(Optional.of(expected));

			// when
			Deposit actual = depositApplication.getByCode(depositCode);

			// then
			assertThat(actual).isNotNull();
			assertThat(actual.getUserCode()).isEqualTo("USER_CODE");
			then(depositPersistencePort).should().getDeposit(depositCode);
		}

		@DisplayName("예치금 코드와 금액을 입력하면, 잔액이 충분한 경우 true를 반환한다.")
		@Test
		void givenDepositCodeAndAmount_whenCheckingBalance_thenReturnsTrue() {
			// given
			String depositCode = "DEPOSIT_CODE";
			Long amount = 30000L;
			Deposit deposit = new Deposit("USER_CODE");
			deposit.charge(50000L); // 잔액 50,000원

			given(depositPersistencePort.getDeposit(depositCode))
				.willReturn(Optional.of(deposit));

			// when
			boolean actual = depositApplication.hasEnoughBalance(depositCode, amount);

			// then
			assertThat(actual).isTrue();
			then(depositPersistencePort).should().getDeposit(depositCode);
		}

		@DisplayName("예치금 코드와 금액을 입력하면, 잔액이 부족한 경우 false를 반환한다.")
		@Test
		void givenDepositCodeAndAmount_whenCheckingBalance_thenReturnsFalse() {
			// given
			String depositCode = "VALID_CODE";
			Long amount = 100000L;
			Deposit deposit = new Deposit("USER_CODE");
			deposit.charge(50000L); // 잔액 50,000원

			given(depositPersistencePort.getDeposit(depositCode))
				.willReturn(Optional.of(deposit));

			// when
			boolean actual = depositApplication.hasEnoughBalance(depositCode, amount);

			// then
			assertThat(actual).isFalse();
			then(depositPersistencePort).should().getDeposit(depositCode);
		}

		@DisplayName("존재하지 않는 예치금 코드로 잔액을 확인하면, 예외를 발생시킨다.")
		@Test
		void givenInvalidDepositCode_whenCheckingBalance_thenThrowsException() {
			// given
			String depositCode = "INVALID_CODE";
			Long amount = 10000L;

			given(depositPersistencePort.getDeposit(depositCode))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> depositApplication.hasEnoughBalance(depositCode, amount))
				.isInstanceOf(ServiceException.class);

			then(depositPersistencePort).should().getDeposit(depositCode);
		}
	}

	@Nested
	@DisplayName("충전 기능은")
	class describe_charge {

		@DisplayName("사용자 코드와 금액을 입력하면, 충전 후 거래 내역을 반환한다.")
		@Test
		void givenUserCodeAndAmount_whenCharging_thenReturnsHistory() {
			// given
			String userCode = "USER_CODE";
			Long amount = 10000L;
			Deposit deposit = new Deposit(userCode);

			// DepositHistory는 Deposit 객체가 필요하므로 Mock 설정
			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.of(deposit));
			given(depositCommandPort.saveDeposit(any(Deposit.class)))
				.willReturn(deposit);
			given(depositHistoryCommandPort.saveDepositHistory(any(DepositHistory.class)))
				.willAnswer(invocation -> invocation.getArgument(0));

			// when
			DepositHistory actual = depositEventApplication.charge(userCode, DepositHistoryType.CHARGE_TOSS, amount);

			// then
			assertThat(actual).isNotNull();
			assertThat(actual.getUserCode()).isEqualTo(userCode);
			assertThat(actual.getAmount()).isEqualTo(amount);
			assertThat(actual.getType()).isEqualTo(DepositHistoryType.CHARGE_TOSS);
			then(depositPersistencePort).should().getDepositByUserCode(userCode);
			then(depositCommandPort).should().saveDeposit(any(Deposit.class));
			then(depositHistoryCommandPort).should().saveDepositHistory(any(DepositHistory.class));
		}

		@DisplayName("존재하지 않는 사용자 코드로 충전하면, 예외를 발생시킨다.")
		@Test
		void givenNonExistentUserCode_whenCharging_thenThrowsException() {
			// given
			String userCode = "NON_EXIST_USER_CODE";
			Long amount = 10000L;

			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> depositEventApplication.charge(userCode, DepositHistoryType.CHARGE_TOSS, amount))
				.isInstanceOf(ServiceException.class);

			then(depositPersistencePort).should().getDepositByUserCode(userCode);
			then(depositHistoryCommandPort).should(never()).saveDepositHistory(any(DepositHistory.class));
		}
	}

	@Nested
	@DisplayName("출금 기능은")
	class describe_withdraw {

		@DisplayName("사용자 코드와 금액을 입력하면, 출금 후 거래 내역을 반환한다.")
		@Test
		void givenUserCodeAndAmount_whenWithdrawing_thenReturnsHistory() {
			// given
			String userCode = "USER_CODE";
			Long amount = 5000L;
			Deposit deposit = new Deposit(userCode);
			deposit.charge(10000L); // 초기 잔액 설정

			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.of(deposit));
			given(depositCommandPort.saveDeposit(any(Deposit.class)))
				.willReturn(deposit);
			given(depositHistoryCommandPort.saveDepositHistory(any(DepositHistory.class)))
				.willAnswer(invocation -> invocation.getArgument(0));

			// when
			DepositHistory actual = depositEventApplication.withdraw(userCode, DepositHistoryType.PAYMENT_TOSS, amount);

			// then
			assertThat(actual).isNotNull();
			assertThat(actual.getUserCode()).isEqualTo(userCode);
			assertThat(actual.getAmount()).isEqualTo(amount);
			assertThat(actual.getType()).isEqualTo(DepositHistoryType.PAYMENT_TOSS);
			then(depositPersistencePort).should().getDepositByUserCode(userCode);
			then(depositCommandPort).should().saveDeposit(any(Deposit.class));
			then(depositHistoryCommandPort).should().saveDepositHistory(any(DepositHistory.class));
		}

		@DisplayName("존재하지 않는 사용자 코드로 출금하면, 예외를 발생시킨다.")
		@Test
		void givenNonExistentUserCode_whenWithdrawing_thenThrowsException() {
			// given
			String userCode = "NON_EXIST_USER_CODE";
			Long amount = 5000L;

			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> depositEventApplication.withdraw(userCode, DepositHistoryType.PAYMENT_TOSS, amount))
				.isInstanceOf(ServiceException.class);

			then(depositPersistencePort).should().getDepositByUserCode(userCode);
			then(depositHistoryCommandPort).should(never()).saveDepositHistory(any(DepositHistory.class));
		}
	}

	@Nested
	@DisplayName("환불 기능은")
	class describe_refund {

		@DisplayName("사용자 코드와 금액을 입력하면, 환불 후 거래 내역을 반환한다.")
		@Test
		void givenUserCodeAndAmount_whenRefunding_thenReturnsHistory() {
			// given
			String userCode = "USER_CODE";
			Long amount = 3000L;
			Deposit deposit = new Deposit(userCode);

			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.of(deposit));
			given(depositCommandPort.saveDeposit(any(Deposit.class)))
				.willReturn(deposit);
			given(depositHistoryCommandPort.saveDepositHistory(any(DepositHistory.class)))
				.willAnswer(invocation -> invocation.getArgument(0));

			// when
			DepositHistory actual = depositEventApplication.refund(userCode, DepositHistoryType.REFUND_TOSS, amount);

			// then
			assertThat(actual).isNotNull();
			assertThat(actual.getUserCode()).isEqualTo(userCode);
			assertThat(actual.getAmount()).isEqualTo(amount);
			assertThat(actual.getType()).isEqualTo(DepositHistoryType.REFUND_TOSS);
			then(depositPersistencePort).should().getDepositByUserCode(userCode);
			then(depositCommandPort).should().saveDeposit(any(Deposit.class));
			then(depositHistoryCommandPort).should().saveDepositHistory(any(DepositHistory.class));
		}

		@DisplayName("존재하지 않는 사용자 코드로 환불하면, 예외를 발생시킨다.")
		@Test
		void givenNonExistentUserCode_whenRefunding_thenThrowsException() {
			// given
			String userCode = "NON_EXIST_USER_CODE";
			Long amount = 3000L;

			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> depositEventApplication.refund(userCode, DepositHistoryType.REFUND_TOSS, amount))
				.isInstanceOf(ServiceException.class);

			then(depositPersistencePort).should().getDepositByUserCode(userCode);
			then(depositHistoryCommandPort).should(never()).saveDepositHistory(any(DepositHistory.class));
		}
	}

	@Nested
	@DisplayName("삭제 기능은")
	class describe_delete {

		@DisplayName("사용자 코드를 입력하면, 예치금을 삭제한다.")
		@Test
		void givenUserCode_whenDeletingDeposit_thenDeletesSuccessfully() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);

			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.of(deposit));
			willDoNothing().given(depositCommandPort).removeDeposit(anyString());

			// when
			depositEventApplication.deleteDeposit(userCode);

			// then
			then(depositPersistencePort).should().getDepositByUserCode(userCode);
			then(depositCommandPort).should().removeDeposit(deposit.getCode());
		}

		@DisplayName("존재하지 않는 사용자 코드로 삭제하면, 예외를 발생시킨다.")
		@Test
		void givenNonExistentUserCode_whenDeletingDeposit_thenThrowsException() {
			// given
			String userCode = "NON_EXIST_USER_CODE";

			given(depositPersistencePort.getDepositByUserCode(userCode))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> depositEventApplication.deleteDeposit(userCode))
				.isInstanceOf(ServiceException.class);

			then(depositPersistencePort).should().getDepositByUserCode(userCode);
			then(depositCommandPort).should(never()).removeDeposit(anyString());
		}
	}

}
