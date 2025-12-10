package io.devground.payments.deposit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.devground.payments.deposit.domain.deposit.Deposit;

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
			new Deposit(userCode);

			// when

			// then
		}

		@DisplayName("초기 잔액은 0원이다.")
		@Test
		void givenNewDeposit_whenCreated_thenBalanceIsZero() {
			// given

			// when

			// then
		}
	}

	@Nested
	@DisplayName("충전 기능은")
	class describe_charge {

		@DisplayName("양수 금액을 입력하면, 잔액이 증가한다.")
		@Test
		void givenPositiveAmount_whenCharging_thenBalanceIncreases() {
			// given

			// when

			// then
		}

		@DisplayName("0원을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenZeroAmount_whenCharging_thenThrowsException() {
			// given

			// when & then
		}

		@DisplayName("음수 금액을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenNegativeAmount_whenCharging_thenThrowsException() {
			// given

			// when & then
		}

		@DisplayName("null을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenNullAmount_whenCharging_thenThrowsException() {
			// given

			// when & then
		}
	}

	@Nested
	@DisplayName("출금 기능은")
	class describe_withdraw {

		@DisplayName("잔액이 충분하면, 출금에 성공한다.")
		@Test
		void givenSufficientBalance_whenWithdrawing_thenSucceeds() {
			// given

			// when

			// then
		}

		@DisplayName("잔액이 부족하면, 예외를 발생시킨다.")
		@Test
		void givenInsufficientBalance_whenWithdrawing_thenThrowsException() {
			// given

			// when & then
		}

		@DisplayName("0원을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenZeroAmount_whenWithdrawing_thenThrowsException() {
			// given

			// when & then
		}

		@DisplayName("음수 금액을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenNegativeAmount_whenWithdrawing_thenThrowsException() {
			// given

			// when & then
		}

		@DisplayName("null을 입력하면, 예외를 발생시킨다.")
		@Test
		void givenNullAmount_whenWithdrawing_thenThrowsException() {
			// given

			// when & then
		}
	}

	@Nested
	@DisplayName("Getter 메서드는")
	class describe_getters {

		@DisplayName("예치금 코드를 반환한다.")
		@Test
		void whenGettingCode_thenReturnsCode() {
			// given

			// when

			// then
		}

		@DisplayName("사용자 코드를 반환한다.")
		@Test
		void whenGettingUserCode_thenReturnsUserCode() {
			// given

			// when

			// then
		}

		@DisplayName("잔액을 반환한다.")
		@Test
		void whenGettingBalance_thenReturnsBalance() {
			// given

			// when

			// then
		}
	}

}
