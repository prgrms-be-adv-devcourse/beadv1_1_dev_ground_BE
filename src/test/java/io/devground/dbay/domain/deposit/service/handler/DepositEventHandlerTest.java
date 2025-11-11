package io.devground.dbay.domain.deposit.service.handler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import io.devground.core.commands.CreateDeposit;
import io.devground.core.events.deposit.DepositCreateFailed;
import io.devground.core.events.deposit.DepositCreatedSuccess;
import io.devground.dbay.domain.deposit.dto.response.DepositResponse;
import io.devground.dbay.domain.deposit.service.DepositService;

@ExtendWith(MockitoExtension.class)
class DepositEventHandlerTest {

	@Mock
	private DepositService depositService;

	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;

	@InjectMocks
	private DepositEventHandler depositEventHandler;

	private String depositsEventTopicName = "deposits-events";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(depositEventHandler, "depositsEventTopicName", depositsEventTopicName);
	}

	@Test
	@DisplayName("CreateDeposit command를 처리하면 예치금 생성 후 DepositCreatedSuccess 이벤트를 발행한다")
	void testHandleCreateDepositSuccess() {
		// given
		String userCode = "USER001";
		String depositCode = "DEPOSIT001";
		CreateDeposit command = new CreateDeposit(userCode);

		DepositResponse depositResponse = new DepositResponse(
			1L,
			userCode,
			depositCode,
			0L,
			LocalDateTime.now(),
			LocalDateTime.now()
		);
		when(depositService.createDeposit(userCode)).thenReturn(depositResponse);

		// when
		depositEventHandler.handleCommand(command);

		// then
		verify(depositService).createDeposit(userCode);

		ArgumentCaptor<DepositCreatedSuccess> eventCaptor = ArgumentCaptor.forClass(DepositCreatedSuccess.class);
		verify(kafkaTemplate).send(eq(depositsEventTopicName), eventCaptor.capture());

		DepositCreatedSuccess event = eventCaptor.getValue();
		assertThat(event.userCode()).isEqualTo(userCode);
		assertThat(event.depositCode()).isEqualTo(depositCode);
	}

	@Test
	@DisplayName("CreateDeposit command 처리 중 예외 발생 시 DepositCreateFailed 이벤트를 발행한다")
	void testHandleCreateDepositFailure() {
		// given
		String userCode = "USER001";
		CreateDeposit command = new CreateDeposit(userCode);

		when(depositService.createDeposit(userCode))
			.thenThrow(new RuntimeException("예치금 생성 실패"));

		// when
		depositEventHandler.handleCommand(command);

		// then
		verify(depositService).createDeposit(userCode);

		ArgumentCaptor<DepositCreateFailed> eventCaptor = ArgumentCaptor.forClass(DepositCreateFailed.class);
		verify(kafkaTemplate).send(eq(depositsEventTopicName), eventCaptor.capture());

		DepositCreateFailed event = eventCaptor.getValue();
		assertThat(event.userCode())
			.isEqualTo(userCode);
		assertThat(event.msg())
			.isEqualTo("예치금 생성에 실패했어요");
	}

	@Test
	@DisplayName("null userCode로 CreateDeposit command 처리 시 DepositCreateFailed 이벤트를 발행한다")
	void testHandleCreateDepositWithNullUserCode() {
		// given
		CreateDeposit command = new CreateDeposit(null);

		when(depositService.createDeposit(null))
			.thenThrow(new IllegalArgumentException("userCode는 필수입니다"));

		// when
		depositEventHandler.handleCommand(command);

		// then
		verify(depositService).createDeposit(null);

		ArgumentCaptor<DepositCreateFailed> eventCaptor = ArgumentCaptor.forClass(DepositCreateFailed.class);
		verify(kafkaTemplate).send(eq(depositsEventTopicName), eventCaptor.capture());

		DepositCreateFailed event = eventCaptor.getValue();
		assertThat(event.userCode()).isNull();
		assertThat(event.msg()).isEqualTo("예치금 생성에 실패했어요");
	}
}