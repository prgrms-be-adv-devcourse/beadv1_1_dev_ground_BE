package io.devground.payments.deposit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.devground.payments.deposit.domain.deposit.Deposit;
import io.devground.payments.deposit.domain.exception.DomainException;

@DisplayName("도메인 - Deposit")
class DepositTest {

	@Nested
	@DisplayName("Deposit 생성 시")
	class describe_constructor {

		@DisplayName("유효한 사용자 코드를 입력하면, 예치금이 생성된다.")
		@Test
		void givenValidUserCode_whenCreating_thenCreatesDeposit() {
			// given
			String userCode = "USER_CODE";

			// when
			Deposit deposit = new Deposit(userCode);

			// then
			assertThat(deposit)
				.isNotNull()
				.returns(userCode, Deposit::getUserCode)
				.extracting(Deposit::getCode, Deposit::getCreatedAt, Deposit::getUpdatedAt)
				.doesNotContainNull();
		}

		@DisplayName("초기 잔액은 0원이다.")
		@Test
		void givenNewDeposit_whenCreated_thenBalanceIsZero() {
			// given
			String userCode = "USER_CODE";

			// when
			Deposit deposit = new Deposit(userCode);

			// then
			assertThat(deposit.getBalance())
				.isZero();
		}
	}

	@Nested
	@DisplayName("충전 기능은")
	class describe_charge {

		@DisplayName("양수 금액을 입력하면, 잔액이 증가한다.")
		@Test
		void givenPositiveAmount_whenCharging_thenBalanceIncreases() {
			// given
			Deposit deposit = new Deposit("USER_CODE");
			Long chargeAmount = 10000L;

			// when
			deposit.charge(chargeAmount);

			// then
			assertThat(deposit.getBalance())
				.isEqualTo(10000L);
		}

		@DisplayName("0원을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenZeroAmount_whenCharging_thenThrowsException() {
			// given
			Deposit deposit = new Deposit("USER_CODE");

			// when & then
			assertThatThrownBy(() -> deposit.charge(0L))
				.isInstanceOf(DomainException.class);
		}

		@DisplayName("음수 금액을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenNegativeAmount_whenCharging_thenThrowsException() {
			// given
			Deposit deposit = new Deposit("USER_CODE");

			// when & then
			assertThatThrownBy(() -> deposit.charge(-1000L))
				.isInstanceOf(DomainException.class);
		}

		@DisplayName("null을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenNullAmount_whenCharging_thenThrowsException() {
			// given
			Deposit deposit = new Deposit("USER_CODE");

			// when & then
			assertThatThrownBy(() -> deposit.charge(null))
				.isInstanceOf(DomainException.class);
		}
	}

	@Nested
	@DisplayName("출금 기능은")
	class describe_withdraw {

		@DisplayName("잔액이 충분하면, 출금에 성공한다.")
		@Test
		void givenSufficientBalance_whenWithdrawing_thenSucceeds() {
			// given
			Deposit deposit = new Deposit("USER_CODE");
			deposit.charge(10000L);
			Long withdrawAmount = 5000L;

			// when
			deposit.withdraw(withdrawAmount);

			// then
			assertThat(deposit.getBalance())
				.isEqualTo(5000L);
		}

		@DisplayName("잔액이 부족하면, 예외를 발생시킨다.")
		@Test
		void givenInsufficientBalance_whenWithdrawing_thenThrowsException() {
			// given
			Deposit deposit = new Deposit("USER_CODE");
			deposit.charge(5000L);

			// when & then
			assertThatThrownBy(() -> deposit.withdraw(10000L))
				.isInstanceOf(DomainException.class);
		}

		@DisplayName("0원을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenZeroAmount_whenWithdrawing_thenThrowsException() {
			// given
			Deposit deposit = new Deposit("USER_CODE");
			deposit.charge(10000L);

			// when & then
			assertThatThrownBy(() -> deposit.withdraw(0L))
				.isInstanceOf(DomainException.class);
		}

		@DisplayName("음수 금액을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenNegativeAmount_whenWithdrawing_thenThrowsException() {
			// given
			Deposit deposit = new Deposit("USER_CODE");
			deposit.charge(10000L);

			// when & then
			assertThatThrownBy(() -> deposit.withdraw(-1000L))
				.isInstanceOf(DomainException.class);
		}

		@DisplayName("null을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenNullAmount_whenWithdrawing_thenThrowsException() {
			// given
			Deposit deposit = new Deposit("USER_CODE");
			deposit.charge(10000L);

			// when & then
			assertThatThrownBy(() -> deposit.withdraw(null))
				.isInstanceOf(DomainException.class);
		}
	}

	@Nested
	@DisplayName("Getter 메서드는")
	class describe_getters {

		@DisplayName("예치금 코드를 반환한다.")
		@Test
		void whenGettingCode_thenReturnsCode() {
			// given
			Deposit deposit = new Deposit("USER_CODE");

			// when
			String code = deposit.getCode();

			// then
			assertThat(code)
				.isNotNull()
				.isNotEmpty();
		}

		@DisplayName("사용자 코드를 반환한다.")
		@Test
		void whenGettingUserCode_thenReturnsUserCode() {
			// given
			String expectedUserCode = "USER_CODE";
			Deposit deposit = new Deposit(expectedUserCode);

			// when
			String userCode = deposit.getUserCode();

			// then
			assertThat(userCode)
				.isEqualTo(expectedUserCode);
		}

		@DisplayName("잔액을 반환한다.")
		@Test
		void whenGettingBalance_thenReturnsBalance() {
			// given
			Deposit deposit = new Deposit("USER_CODE");
			deposit.charge(15000L);

			// when
			Long balance = deposit.getBalance();

			// then
			assertThat(balance)
				.isEqualTo(15000L);
		}
	}

}