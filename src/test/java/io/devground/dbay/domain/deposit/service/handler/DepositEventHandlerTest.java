package io.devground.dbay.domain.deposit.service.handler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import io.devground.core.commands.deposit.ChargeDeposit;
import io.devground.core.commands.deposit.CreateDeposit;
import io.devground.core.commands.deposit.DeleteDeposit;
import io.devground.core.commands.deposit.RefundDeposit;
import io.devground.core.commands.deposit.WithdrawDeposit;
import io.devground.core.events.deposit.DepositChargeFailed;
import io.devground.core.events.deposit.DepositChargedSuccess;
import io.devground.core.events.deposit.DepositCreateFailed;
import io.devground.core.events.deposit.DepositCreatedSuccess;
import io.devground.core.events.deposit.DepositDeleteFailed;
import io.devground.core.events.deposit.DepositDeletedSuccess;
import io.devground.core.events.deposit.DepositRefundFailed;
import io.devground.core.events.deposit.DepositRefundedSuccess;
import io.devground.core.events.deposit.DepositWithdrawFailed;
import io.devground.core.events.deposit.DepositWithdrawnSuccess;
import io.devground.dbay.domain.deposit.dto.response.DepositHistoryResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositResponse;
import io.devground.dbay.domain.deposit.entity.vo.DepositHistoryType;
import io.devground.dbay.domain.deposit.service.DepositService;

@DisplayName("비즈니스 로직 - 예치금 이벤트 핸들러")
@ExtendWith(MockitoExtension.class)
class DepositEventHandlerTest {

	@Mock
	private DepositService depositService;

	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;

	private DepositEventHandler depositEventHandler;

	private String depositsEventTopicName;

	@BeforeEach
	void setUp() {
		depositsEventTopicName = "deposits-events";

		depositEventHandler = new DepositEventHandler(
			depositService,
			kafkaTemplate,
			depositsEventTopicName
		);
	}

	@Test
	@DisplayName("CreateDeposit command를 받으면, 예치금을 생성하고 DepositCreatedSuccess 이벤트를 발행한다.")
	void givenCreateDepositCommand_whenHandlingCommand_thenPublishesDepositCreatedSuccess() {
		// given
		String userCode = "USER_CODE";
		String depositCode = "DEPOSIT_CODE";
		CreateDeposit command = new CreateDeposit(userCode);
		DepositResponse depositResponse = new DepositResponse(
			1L,
			userCode,
			depositCode,
			0L,
			LocalDateTime.now(),
			LocalDateTime.now()
		);

		given(depositService.createDeposit(userCode))
			.willReturn(depositResponse);

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().createDeposit(userCode);

		DepositCreatedSuccess event = capturePublishedEvent(DepositCreatedSuccess.class);
		assertThat(event)
			.hasFieldOrPropertyWithValue("userCode", userCode)
			.hasFieldOrPropertyWithValue("depositCode", depositCode);
	}

	@Test
	@DisplayName("예치금 생성 중 예외가 발생하면, DepositCreateFailed 이벤트를 발행한다.")
	void givenException_whenHandlingCommand_thenPublishesDepositCreateFailed() {
		// given
		String userCode = "USER_CODE";
		CreateDeposit command = new CreateDeposit(userCode);

		given(depositService.createDeposit(userCode))
			.willThrow(new RuntimeException("예치금 생성 실패"));

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().createDeposit(userCode);

		DepositCreateFailed event = capturePublishedEvent(DepositCreateFailed.class);
		assertThat(event)
			.hasFieldOrPropertyWithValue("userCode", userCode);
	}

	@Test
	@DisplayName("null userCode를 받으면, DepositCreateFailed 이벤트를 발행한다.")
	void givenNullUserCode_whenHandlingCommand_thenPublishesDepositCreateFailed() {
		// given
		CreateDeposit command = new CreateDeposit(null);

		given(depositService.createDeposit(null))
			.willThrow(new IllegalArgumentException("userCode는 필수입니다"));

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().createDeposit(null);

		DepositCreateFailed event = capturePublishedEvent(DepositCreateFailed.class);
		assertThat(event)
			.hasFieldOrPropertyWithValue("userCode", null);
	}

	@Test
	@DisplayName("ChargeDeposit command를 받으면, 예치금을 충전하고 DepositCharged 이벤트를 발행한다.")
	void givenChargeDepositCommand_whenHandlingCommand_thenPublishesDepositCharged() {
		// given
		String userCode = "USER_CODE";
		String depositCode = "DEPOSIT_CODE";
		Long amount = 10000L;
		Long balanceAfter = 10000L;
		io.devground.core.model.vo.DepositHistoryType type = io.devground.core.model.vo.DepositHistoryType.CHARGE_TOSS;
		ChargeDeposit command = new ChargeDeposit(userCode, amount, type);
		DepositHistoryResponse historyResponse = new DepositHistoryResponse(
			2L,
			depositCode,
			1L,
			userCode,
			null,
			null,
			amount,
			balanceAfter,
			DepositHistoryType.CHARGE_TOSS,
			"토스 충전",
			LocalDateTime.now()
		);

		given(depositService.charge(userCode, DepositHistoryType.CHARGE_TOSS, amount))
			.willReturn(historyResponse);

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().charge(userCode, DepositHistoryType.CHARGE_TOSS, amount);

		DepositChargedSuccess event = capturePublishedEvent(DepositChargedSuccess.class);
		assertThat(event)
			.hasFieldOrPropertyWithValue("userCode", userCode)
			.hasFieldOrPropertyWithValue("depositHistoryCode", depositCode)
			.hasFieldOrPropertyWithValue("amount", amount)
			.hasFieldOrPropertyWithValue("balanceAfter", balanceAfter);
	}

	@Test
	@DisplayName("충전 중 예외가 발생하면, DepositChargeFailed 이벤트를 발행한다.")
	void givenException_whenChargingDeposit_thenPublishesDepositChargeFailed() {
		// given
		String userCode = "USER_CODE";
		Long amount = 10000L;
		io.devground.core.model.vo.DepositHistoryType type = io.devground.core.model.vo.DepositHistoryType.CHARGE_TOSS;
		ChargeDeposit command = new ChargeDeposit(userCode, amount, type);

		given(depositService.charge(userCode, DepositHistoryType.CHARGE_TOSS, amount))
			.willThrow(new RuntimeException("충전 실패"));

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().charge(userCode, DepositHistoryType.CHARGE_TOSS, amount);

		DepositChargeFailed event = capturePublishedEvent(DepositChargeFailed.class);
		assertThat(event)
			.hasFieldOrPropertyWithValue("userCode", userCode)
			.hasFieldOrPropertyWithValue("amount", amount);
	}

	@Test
	@DisplayName("WithdrawDeposit command를 받으면, 예치금을 인출하고 DepositWithdrawn 이벤트를 발행한다.")
	void givenWithdrawDepositCommand_whenHandlingCommand_thenPublishesDepositWithdrawn() {
		// given
		String userCode = "USER_CODE";
		String depositCode = "DEPOSIT_CODE";
		Long amount = 5000L;
		Long balanceAfter = 5000L;
		io.devground.core.model.vo.DepositHistoryType type = io.devground.core.model.vo.DepositHistoryType.PAYMENT_TOSS;
		String orderCode = "ORDER_CODE";

		WithdrawDeposit command = new WithdrawDeposit(userCode, amount, type, orderCode);
		DepositHistoryResponse historyResponse = new DepositHistoryResponse(
			2L,
			depositCode,
			1L,
			userCode,
			null,
			null,
			amount,
			balanceAfter,
			DepositHistoryType.PAYMENT_TOSS,
			"토스 결제",
			LocalDateTime.now()
		);

		given(depositService.withdraw(userCode, DepositHistoryType.PAYMENT_TOSS, amount))
			.willReturn(historyResponse);

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().withdraw(userCode, DepositHistoryType.PAYMENT_TOSS, amount);

		DepositWithdrawnSuccess event = capturePublishedEvent(DepositWithdrawnSuccess.class);
		assertThat(event)
			.hasFieldOrPropertyWithValue("userCode", userCode)
			.hasFieldOrPropertyWithValue("depositHistoryCode", depositCode)
			.hasFieldOrPropertyWithValue("amount", amount)
			.hasFieldOrPropertyWithValue("balanceAfter", balanceAfter);
	}

	@Test
	@DisplayName("인출 중 예외가 발생하면, DepositWithdrawFailed 이벤트를 발행한다.")
	void givenException_whenWithdrawingDeposit_thenPublishesDepositWithdrawFailed() {
		// given
		String userCode = "USER_CODE";
		Long amount = 5000L;
		io.devground.core.model.vo.DepositHistoryType type = io.devground.core.model.vo.DepositHistoryType.PAYMENT_TOSS;
		String orderCode = "ORDER_CODE";

		WithdrawDeposit command = new WithdrawDeposit(userCode, amount, type, orderCode);

		given(depositService.withdraw(userCode, DepositHistoryType.PAYMENT_TOSS, amount))
			.willThrow(new RuntimeException("인출 실패"));

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().withdraw(userCode, DepositHistoryType.PAYMENT_TOSS, amount);

		DepositWithdrawFailed event = capturePublishedEvent(DepositWithdrawFailed.class);
		assertThat(event)
			.hasFieldOrPropertyWithValue("userCode", userCode)
			.hasFieldOrPropertyWithValue("amount", amount);
	}

	@Test
	@DisplayName("RefundDeposit command를 받으면, 예치금을 환불하고 DepositRefunded 이벤트를 발행한다.")
	void givenRefundDepositCommand_whenHandlingCommand_thenPublishesDepositRefunded() {
		// given
		String userCode = "USER_CODE";
		String depositCode = "DEPOSIT_CODE";
		Long amount = 3000L;
		Long balanceAfter = 8000L;
		io.devground.core.model.vo.DepositHistoryType type = io.devground.core.model.vo.DepositHistoryType.REFUND_TOSS;
		RefundDeposit command = new RefundDeposit(userCode, amount, type);
		DepositHistoryResponse historyResponse = new DepositHistoryResponse(
			2L,
			depositCode,
			1L,
			userCode,
			null,
			null,
			amount,
			balanceAfter,
			DepositHistoryType.REFUND_TOSS,
			"토스 환불",
			LocalDateTime.now()
		);

		given(depositService.refund(userCode, DepositHistoryType.REFUND_TOSS, amount))
			.willReturn(historyResponse);

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().refund(userCode, DepositHistoryType.REFUND_TOSS, amount);

		DepositRefundedSuccess event = capturePublishedEvent(DepositRefundedSuccess.class);
		assertThat(event)
			.hasFieldOrPropertyWithValue("userCode", userCode)
			.hasFieldOrPropertyWithValue("depositHistoryCode", depositCode)
			.hasFieldOrPropertyWithValue("amount", amount)
			.hasFieldOrPropertyWithValue("balanceAfter", balanceAfter);
	}

	@Test
	@DisplayName("환불 중 예외가 발생하면, DepositRefundFailed 이벤트를 발행한다.")
	void givenException_whenRefundingDeposit_thenPublishesDepositRefundFailed() {
		// given
		String userCode = "USER_CODE";
		Long amount = 3000L;
		io.devground.core.model.vo.DepositHistoryType type = io.devground.core.model.vo.DepositHistoryType.REFUND_TOSS;
		RefundDeposit command = new RefundDeposit(userCode, amount, type);

		given(depositService.refund(userCode, DepositHistoryType.REFUND_TOSS, amount))
			.willThrow(new RuntimeException("환불 실패"));

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().refund(userCode, DepositHistoryType.REFUND_TOSS, amount);

		DepositRefundFailed event = capturePublishedEvent(DepositRefundFailed.class);
		assertThat(event)
			.hasFieldOrPropertyWithValue("userCode", userCode)
			.hasFieldOrPropertyWithValue("amount", amount);
	}

	@Test
	@DisplayName("DeleteDeposit command를 받으면, 예치금을 삭제하고 DepositDeletedSuccess 이벤트를 발행한다.")
	void givenDeleteDepositCommand_whenHandlingCommand_thenPublishesDepositDeletedSuccess() {
		// given
		String userCode = "USER_CODE";
		DeleteDeposit command = new DeleteDeposit(userCode);

		willDoNothing().given(depositService)
			.deleteDeposit(userCode);

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().deleteDeposit(userCode);
		then(kafkaTemplate).should()
			.send(eq(depositsEventTopicName), any(DepositDeletedSuccess.class));
	}

	@Test
	@DisplayName("삭제 중 예외가 발생하면, DepositDeleteFailed 이벤트를 발행한다.")
	void givenException_whenDeletingDeposit_thenPublishesDepositDeleteFailed() {
		// given
		String userCode = "USER_CODE";
		DeleteDeposit command = new DeleteDeposit(userCode);

		willThrow(new RuntimeException("삭제 실패"))
			.given(depositService).deleteDeposit(userCode);

		// when
		depositEventHandler.handleCommand(command);

		// then
		then(depositService).should().deleteDeposit(userCode);
		then(kafkaTemplate).should()
			.send(eq(depositsEventTopicName), any(DepositDeleteFailed.class));
	}

	private <T> T capturePublishedEvent(Class<T> eventClass) {
		ArgumentCaptor<T> captor = ArgumentCaptor.forClass(eventClass);
		then(kafkaTemplate).should().send(eq(depositsEventTopicName), captor.capture());
		return captor.getValue();
	}
}
