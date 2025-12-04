package io.devground.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.devground.core.commands.deposit.RefundDeposit;
import io.devground.core.commands.payment.DepositRefundCommand;
import io.devground.core.model.vo.DepositHistoryType;
import io.devground.payment.model.dto.request.RefundRequest;
import io.devground.payment.saga.PaymentKafkaHandler;

@ExtendWith(SpringExtension.class)
public class PaymentServiceTests {

	@Mock
	PaymentService paymentService;

	@Mock
	KafkaTemplate<String, Object> kafkaTemplate;

	@InjectMocks
	PaymentKafkaHandler handler; // 네 @KafkaHandler 가 들어있는 클래스 이름

	@Captor
	ArgumentCaptor<RefundRequest> refundRequestCaptor;

	@Captor
	ArgumentCaptor<RefundDeposit> refundDepositCaptor;

	@Test
	@DisplayName("DepositRefundCommand 수신 시 결제 취소 이력 저장 후 RefundDeposit 발행")
	void handleDepositRefundCommand() {
		// given
		DepositRefundCommand command =
			new DepositRefundCommand("USER-001", 10_000L, "ORDER-123" );

		when(kafkaTemplate.send(eq("kafka_test"), any(RefundDeposit.class)))
			.thenReturn(null);

		// when
		handler.handleEvent(command);

		// then 1) paymentService.refund() 에 올바른 값이 넘어갔는지
		verify(paymentService).refund(refundRequestCaptor.capture());
		RefundRequest request = refundRequestCaptor.getValue();

		assertEquals("USER-001", request.userCode());
		assertEquals("ORDER-123", request.orderCode());
		assertEquals(10_000L, request.amount());

		// then 2) kafkaTemplate.send() 로 RefundDeposit 이 잘 나갔는지
		verify(kafkaTemplate).send(any(), refundDepositCaptor.capture());
		RefundDeposit event = refundDepositCaptor.getValue();

		assertEquals("USER-001", event.userCode());
		assertEquals(10_000L, event.amount());
		assertEquals(DepositHistoryType.REFUND_INTERNAL, event.type());
	}
}
