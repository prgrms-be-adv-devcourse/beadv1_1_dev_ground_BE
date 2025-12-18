package io.devground.payments.deposit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.devground.payments.deposit.domain.deposit.Deposit;
import io.devground.payments.deposit.domain.depositHistory.DepositHistory;
import io.devground.payments.deposit.domain.depositHistory.DepositHistoryType;
import io.devground.payments.deposit.domain.exception.DomainException;
import io.devground.payments.deposit.domain.exception.vo.DomainErrorCode;

@DisplayName("도메인 - DepositHistory")
class DepositHistoryTest {

	@Nested
	@DisplayName("Builder를 사용한 생성 시")
	class describe_builder {

		@DisplayName("서로 다른 예치금 간 내부 결제 거래 내역을 생성할 수 있다.")
		@Test
		void givenAllFields_whenBuilding_thenCreatesDepositHistory() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);
			deposit.charge(10000L);  // 초기 잔액 설정

			Deposit payerDeposit = new Deposit("PAYER_CODE");
			payerDeposit.charge(20000L);

			Deposit payeeDeposit = new Deposit("PAYEE_CODE");

			Long amount = 5000L;
			DepositHistoryType type = DepositHistoryType.PAYMENT_INTERNAL;
			String description = "예치금 내부 결제";

			// when
			DepositHistory history = DepositHistory.builder()
				.userCode(userCode)
				.deposit(deposit)
				.payerDeposit(payerDeposit)
				.payeeDeposit(payeeDeposit)
				.amount(amount)
				.type(type)
				.description(description)
				.build();

			// then
			assertThat(history)
				.isNotNull()
				.returns(userCode, DepositHistory::getUserCode)
				.returns(deposit.getCode(), DepositHistory::getDepositCode)
				.returns(payerDeposit.getCode(), DepositHistory::getPayerDepositCode)
				.returns(payeeDeposit.getCode(), DepositHistory::getPayeeDepositCode)
				.returns(amount, DepositHistory::getAmount)
				.returns(type, DepositHistory::getType)
				.returns(description, DepositHistory::getDescription)
				.returns(5000L, DepositHistory::getBalanceAfter);

			assertThat(history.getCode()).isNotNull();
			assertThat(history.getCreatedAt()).isNotNull();
			assertThat(history.getUpdatedAt()).isNotNull();
		}
	}

	@Nested
	@DisplayName("충전 타입 거래 내역은")
	class describe_chargeType {

		@DisplayName("잔액이 0원일 때, 토스로 충전하면 거래 내역이 생성되고 잔액이 증가한다.")
		@Test
		void givenChargeTossType_whenCreating_thenCreatesHistory() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);
			Long chargeAmount = 10000L;
			String description = "토스 결제 충전 10,000원";

			// when
			DepositHistory history = DepositHistory.builder()
				.userCode(userCode)
				.deposit(deposit)
				.payerDeposit(deposit)
				.payeeDeposit(deposit)
				.amount(chargeAmount)
				.type(DepositHistoryType.CHARGE_TOSS)
				.description(description)
				.build();

			// then
			assertThat(history)
				.isNotNull()
				.returns(userCode, DepositHistory::getUserCode)
				.returns(deposit.getCode(), DepositHistory::getDepositCode)
				.returns(chargeAmount, DepositHistory::getAmount)
				.returns(deposit.getCode(), DepositHistory::getPayeeDepositCode)
				.returns(DepositHistoryType.CHARGE_TOSS, DepositHistory::getType)
				.returns(description, DepositHistory::getDescription)
				.returns(10000L, DepositHistory::getBalanceAfter);

			assertThat(history.getCode()).isNotNull();
			assertThat(history.getCreatedAt()).isNotNull();
			assertThat(history.getUpdatedAt()).isNotNull();
		}

		@DisplayName("기존 잔액이 있는 상태에서 충전하면, 잔액이 누적된다.")
		@Test
		void givenExistingBalance_whenCharging_thenBalanceAccumulates() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);
			deposit.charge(10000L);

			Long chargeAmount = 10000L;
			String description = "토스 결제 충전 10,000원";

			// when
			DepositHistory history = DepositHistory.builder()
				.userCode(userCode)
				.deposit(deposit)
				.payerDeposit(deposit)
				.payeeDeposit(deposit)
				.amount(chargeAmount)
				.type(DepositHistoryType.CHARGE_TOSS)
				.description(description)
				.build();

			// then
			assertThat(history)
				.isNotNull()
				.returns(userCode, DepositHistory::getUserCode)
				.returns(deposit.getCode(), DepositHistory::getDepositCode)
				.returns(10000L, DepositHistory::getAmount)
				.returns(deposit.getCode(), DepositHistory::getPayeeDepositCode)
				.returns(DepositHistoryType.CHARGE_TOSS, DepositHistory::getType)
				.returns(description, DepositHistory::getDescription)
				.returns(20000L, DepositHistory::getBalanceAfter);

			assertThat(history.getCode()).isNotNull();
			assertThat(history.getCreatedAt()).isNotNull();
			assertThat(history.getUpdatedAt()).isNotNull();
		}

		@DisplayName("기존 잔액이 없는 상태에서 충전하면, 잔액이 입금액과 같다.")
		@Test
		void givenChargeTransferType_whenCreating_thenCreatesHistory() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);
			Long chargeAmount = 10000L;
			String description = "토스 입금 10,000원";

			// when
			DepositHistory history = DepositHistory.builder()
				.userCode(userCode)
				.deposit(deposit)
				.payerDeposit(deposit)
				.payeeDeposit(deposit)
				.amount(chargeAmount)
				.type(DepositHistoryType.CHARGE_TRANSFER)
				.description(description)
				.build();

			// then
			assertThat(history)
				.isNotNull()
				.returns(userCode, DepositHistory::getUserCode)
				.returns(deposit.getCode(), DepositHistory::getDepositCode)
				.returns(10000L, DepositHistory::getAmount)
				.returns(deposit.getCode(), DepositHistory::getPayeeDepositCode)
				.returns(DepositHistoryType.CHARGE_TRANSFER, DepositHistory::getType)
				.returns(description, DepositHistory::getDescription)
				.returns(10000L, DepositHistory::getBalanceAfter);

			assertThat(history.getCode()).isNotNull();
			assertThat(history.getCreatedAt()).isNotNull();
			assertThat(history.getUpdatedAt()).isNotNull();
		}

		@DisplayName("0원 충전 시 예외를 발생시킨다.")
		@Test
		void givenZeroAmount_whenCharging_thenThrowsException() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);

			Long chargeAmount = 0L;
			String description = "토스 충전 0원";

			// when & then
			assertThatThrownBy(() -> DepositHistory.builder()
				.userCode(userCode)
				.deposit(deposit)
				.payerDeposit(deposit)
				.payeeDeposit(deposit)
				.amount(chargeAmount)
				.type(DepositHistoryType.CHARGE_TOSS)
				.description(description)
				.build()
			).isInstanceOf(DomainException.class)
				.hasFieldOrPropertyWithValue("errorCode", DomainErrorCode.AMOUNT_MUST_BE_POSITIVE);
		}

		@DisplayName("음수 금액 충전 시 예외를 발생시킨다.")
		@Test
		void givenNegativeAmount_whenCharging_thenThrowsException() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);

			Long chargeAmount = -1000L;
			String description = "토스 충전 -1000원";

			// when & then
			assertThatThrownBy(() -> DepositHistory.builder()
				.userCode(userCode)
				.deposit(deposit)
				.payerDeposit(deposit)
				.payeeDeposit(deposit)
				.amount(chargeAmount)
				.type(DepositHistoryType.CHARGE_TOSS)
				.description(description)
				.build()
			).isInstanceOf(DomainException.class)
				.hasFieldOrPropertyWithValue("errorCode", DomainErrorCode.AMOUNT_MUST_BE_POSITIVE);
		}
	}

	@Nested
	@DisplayName("출금 타입 거래 내역은")
	class describe_withdrawType {

		@DisplayName("토스 결제로 출금하면, 거래 내역이 생성되고 잔액이 감소한다.")
		@Test
		void givenPaymentTossType_whenCreating_thenCreatesHistory() {
			// given
			String payeeUserCode = "PAYEE_USER_CODE";
			Deposit payeeDeposit = new Deposit(payeeUserCode);

			String payerUserCode = "PAYER_USER_CODE";
			Deposit payerDeposit = new Deposit(payerUserCode);
			payerDeposit.charge(20000L);

			Long paymentAmount = 5000L;
			String description = "토스 결제 5,000원";

			// when
			DepositHistory history = DepositHistory.builder()
				.userCode(payeeUserCode)
				.deposit(payerDeposit)
				.payerDeposit(payerDeposit)
				.payeeDeposit(payeeDeposit)
				.amount(paymentAmount)
				.type(DepositHistoryType.PAYMENT_TOSS)
				.description(description)
				.build();

			// then
			assertThat(history)
				.isNotNull()
				.returns(payeeUserCode, DepositHistory::getUserCode)
				.returns(payerDeposit.getCode(), DepositHistory::getDepositCode)
				.returns(5000L, DepositHistory::getAmount)
				.returns(15000L, DepositHistory::getBalanceAfter)
				.returns(DepositHistoryType.PAYMENT_TOSS, DepositHistory::getType)
				.returns(description, DepositHistory::getDescription);

			assertThat(history.getCode()).isNotNull();
			assertThat(history.getCreatedAt()).isNotNull();
			assertThat(history.getUpdatedAt()).isNotNull();
		}

		@DisplayName("예치금 내부 결제로 출금하면, 거래 내역이 생성되고 잔액이 감소한다.")
		@Test
		void givenPaymentInternalType_whenCreating_thenCreatesHistory() {
			// given
			String payeeUserCode = "PAYEE_USER_CODE";
			Deposit payeeDeposit = new Deposit(payeeUserCode);

			String payerUserCode = "PAYER_USER_CODE";
			Deposit payerDeposit = new Deposit(payerUserCode);
			payerDeposit.charge(20000L);

			Long paymentAmount = 5000L;
			String description = "예치금 결제 5,000원";

			// when
			DepositHistory history = DepositHistory.builder()
				.userCode(payeeUserCode)
				.deposit(payerDeposit)
				.payerDeposit(payerDeposit)
				.payeeDeposit(payeeDeposit)
				.amount(paymentAmount)
				.type(DepositHistoryType.PAYMENT_INTERNAL)
				.description(description)
				.build();

			// then
			assertThat(history).isNotNull()
				.returns(payeeUserCode, DepositHistory::getUserCode)
				.returns(payerDeposit.getCode(), DepositHistory::getDepositCode)
				.returns(5000L, DepositHistory::getAmount)
				.returns(15000L, DepositHistory::getBalanceAfter)
				.returns(DepositHistoryType.PAYMENT_INTERNAL, DepositHistory::getType)
				.returns(description, DepositHistory::getDescription)
				.extracting(DepositHistory::getCode, DepositHistory::getCreatedAt, DepositHistory::getUpdatedAt)
				.doesNotContainNull();

		}

		@DisplayName("잔액이 부족한 상태에서 예치금 출금하면, 예외를 발생시킨다.")
		@Test
		void givenInsufficientBalance_whenWithdrawing_thenThrowsException() {
			// given
			String payeeUserCode = "PAYEE_USER_CODE";
			Deposit payeeDeposit = new Deposit(payeeUserCode);

			String payerUserCode = "PAYER_USER_CODE";
			Deposit payerDeposit = new Deposit(payerUserCode);
			payerDeposit.charge(1000L);

			Long paymentAmount = 5000L;
			String description = "예치금 결제 5,000원";

			// when & then
			assertThatThrownBy(() -> DepositHistory.builder()
				.userCode(payeeUserCode)
				.deposit(payerDeposit)
				.payerDeposit(payerDeposit)
				.payeeDeposit(payeeDeposit)
				.amount(paymentAmount)
				.type(DepositHistoryType.PAYMENT_INTERNAL)
				.description(description)
				.build()
			).isInstanceOf(DomainException.class)
				.hasFieldOrPropertyWithValue("errorCode", DomainErrorCode.INSUFFICIENT_BALANCE);
		}

		@DisplayName("0원으로 예치금 출금하면, 예외를 발생시킨다.")
		@Test
		void givenZeroAmount_whenWithdrawing_thenThrowsException() {
			// given
			String payeeUserCode = "PAYEE_USER_CODE";
			Deposit payeeDeposit = new Deposit(payeeUserCode);

			String payerUserCode = "PAYER_USER_CODE";
			Deposit payerDeposit = new Deposit(payerUserCode);
			payerDeposit.charge(1000L);

			Long paymentAmount = 0L;
			String description = "예치금 결제 0원";

			// when & then
			assertThatThrownBy(() -> DepositHistory.builder()
				.userCode(payeeUserCode)
				.deposit(payerDeposit)
				.payerDeposit(payerDeposit)
				.payeeDeposit(payeeDeposit)
				.amount(paymentAmount)
				.type(DepositHistoryType.PAYMENT_INTERNAL)
				.description(description)
				.build()
			).isInstanceOf(DomainException.class)
				.hasFieldOrPropertyWithValue("errorCode", DomainErrorCode.AMOUNT_MUST_BE_POSITIVE);
		}

		@DisplayName("음수 금액으로 출금하면, 예외를 발생시킨다.")
		@Test
		void givenNegativeAmount_whenWithdrawing_thenThrowsException() {
			// given
			String payeeUserCode = "PAYEE_USER_CODE";
			Deposit payeeDeposit = new Deposit(payeeUserCode);

			String payerUserCode = "PAYER_USER_CODE";
			Deposit payerDeposit = new Deposit(payerUserCode);
			payerDeposit.charge(20000L);

			Long paymentAmount = -5000L;
			String description = "예치금 결제 5,000원";

			// when & then
			assertThatThrownBy(() -> DepositHistory.builder()
				.userCode(payeeUserCode)
				.deposit(payerDeposit)
				.payerDeposit(payerDeposit)
				.payeeDeposit(payeeDeposit)
				.amount(paymentAmount)
				.type(DepositHistoryType.PAYMENT_INTERNAL)
				.description(description)
				.build()
			).isInstanceOf(DomainException.class)
				.hasFieldOrPropertyWithValue("errorCode", DomainErrorCode.AMOUNT_MUST_BE_POSITIVE);
		}
	}

	@Nested
	@DisplayName("환불 타입 거래 내역은")
	class describe_refundType {

		@DisplayName("토스 환불을 하면, 거래 내역이 생성되고 잔액이 증가한다.")
		@Test
		void givenRefundTossType_whenCreating_thenCreatesHistory() {
			// given
			String payeeUserCode = "PAYEE_USER_CODE";
			Deposit payeeDeposit = new Deposit(payeeUserCode);

			String payerUserCode = "PAYER_USER_CODE";
			Deposit payerDeposit = new Deposit(payerUserCode);
			payerDeposit.charge(20000L);

			Long paymentAmount = 5000L;
			String description = "예치금 결제 5,000원";

			// when
			DepositHistory history = DepositHistory.builder()
				.userCode(payeeUserCode)
				.deposit(payerDeposit)
				.payerDeposit(payerDeposit)
				.payeeDeposit(payeeDeposit)
				.amount(paymentAmount)
				.type(DepositHistoryType.REFUND_TOSS)
				.description(description)
				.build();

			// then
			assertThat(history)
				.isNotNull()
				.returns(payeeUserCode, DepositHistory::getUserCode)
				.returns(payerDeposit.getCode(), DepositHistory::getDepositCode)
				.returns(paymentAmount, DepositHistory::getAmount)
				.returns(DepositHistoryType.REFUND_TOSS, DepositHistory::getType)
				.returns(description, DepositHistory::getDescription)
				.returns(25000L, DepositHistory::getBalanceAfter)  // 20,000 + 5,000 = 25,000
				.extracting(DepositHistory::getCode, DepositHistory::getCreatedAt, DepositHistory::getUpdatedAt)
				.doesNotContainNull();
		}

		@DisplayName("예치금 내부 환불을 하면, 거래 내역이 생성되고 잔액이 증가한다.")
		@Test
		void givenRefundInternalType_whenCreating_thenCreatesHistory() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);
			deposit.charge(5000L);  // 기존 잔액 5,000원

			Long refundAmount = 3000L;
			String description = "예치금 환불 3,000원";

			// when
			DepositHistory history = DepositHistory.builder()
				.userCode(userCode)
				.deposit(deposit)
				.payerDeposit(deposit)
				.payeeDeposit(deposit)
				.amount(refundAmount)
				.type(DepositHistoryType.REFUND_INTERNAL)
				.description(description)
				.build();

			// then
			assertThat(history)
				.isNotNull()
				.returns(userCode, DepositHistory::getUserCode)
				.returns(deposit.getCode(), DepositHistory::getDepositCode)
				.returns(refundAmount, DepositHistory::getAmount)
				.returns(DepositHistoryType.REFUND_INTERNAL, DepositHistory::getType)
				.returns(description, DepositHistory::getDescription)
				.returns(8000L, DepositHistory::getBalanceAfter)  // 5,000 + 3,000 = 8,000
				.extracting(DepositHistory::getCode, DepositHistory::getCreatedAt, DepositHistory::getUpdatedAt)
				.doesNotContainNull();
		}

		@DisplayName("0원 환불 시 예외를 발생시킨다.")
		@Test
		void givenZeroAmount_whenRefunding_thenThrowsException() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);

			Long refundAmount = 0L;
			String description = "예치금 환불 0원";

			// when
			assertThatThrownBy(() -> DepositHistory.builder()
				.userCode(userCode)
				.deposit(deposit)
				.payerDeposit(deposit)
				.payeeDeposit(deposit)
				.amount(refundAmount)
				.type(DepositHistoryType.REFUND_INTERNAL)
				.description(description)
				.build()
			).isInstanceOf(DomainException.class);
		}

		@DisplayName("음수 금액 환불 시 예외를 발생시킨다.")
		@Test
		void givenNegativeAmount_whenRefunding_thenThrowsException() {
			// given
			String userCode = "USER_CODE";
			Deposit deposit = new Deposit(userCode);

			Long refundAmount = -1000L;
			String description = "예치금 환불 -1,000원";

			// when
			assertThatThrownBy(() -> DepositHistory.builder()
				.userCode(userCode)
				.deposit(deposit)
				.payerDeposit(deposit)
				.payeeDeposit(deposit)
				.amount(refundAmount)
				.type(DepositHistoryType.REFUND_INTERNAL)
				.description(description)
				.build()
			).isInstanceOf(DomainException.class);
		}
	}

}