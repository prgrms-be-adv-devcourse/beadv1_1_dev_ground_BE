package io.devground.payment.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.devground.core.commands.deposit.RefundDeposit;
import io.devground.core.commands.payment.DepositRefundCommand;
import io.devground.core.model.vo.DepositHistoryType;
import io.devground.payment.model.dto.request.RefundRequest;
import io.devground.payment.model.dto.response.GetPaymentsResponse;
import io.devground.payment.model.entity.Payment;
import io.devground.payment.model.vo.PaymentStatus;
import io.devground.payment.model.vo.PaymentType;
import io.devground.payment.repository.PaymentRepository;
import io.devground.payment.saga.PaymentKafkaHandler;

@ExtendWith(SpringExtension.class)
public class PaymentServiceTests {

	@InjectMocks
	PaymentServiceImpl paymentService;

	@Mock
	private PaymentRepository paymentRepository;

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

	@Test
	@DisplayName("결제 내역 전체 조회 성공")
	void getPayments_success() {
		String userCode = "USER-001";
		String orderCode = "ORDER-123";
		Pageable pageable = PageRequest.of(0, 10);

		Payment p1 = Payment.builder()
			.userCode(userCode)
			.amount(10_000L)
			.build();

		p1.setPaymentStatus(PaymentStatus.PAYMENT_COMPLETED);

		Payment p2 = Payment.builder()
			.userCode(userCode)
			.amount(20_000L)
			.build();

		p2.setPaymentStatus(PaymentStatus.PAYMENT_REFUNDED);


		Page<Payment> paymentPage =
			new PageImpl<>(List.of(p1, p2), pageable, 5);

		given(paymentRepository.findByUserCodeOrderByPaidAtDesc(userCode, pageable))
			.willReturn(paymentPage);

		// when
		Page<GetPaymentsResponse> result =
			paymentService.getPayments(userCode, pageable);

		// then
		// 1) repository 호출 검증 (BDD 스타일)
		then(paymentRepository).should()
			.findByUserCodeOrderByPaidAtDesc(userCode, pageable);
		then(paymentRepository).shouldHaveNoMoreInteractions();

		// 2) Page 메타데이터 검증
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getNumber()).isEqualTo(0);
		assertThat(result.getSize()).isEqualTo(10);


		GetPaymentsResponse r1 = result.getContent().get(0);
		assertThat(r1.amount()).isEqualTo(10_000L);
		assertThat(r1.type()).isEqualTo(PaymentType.DEPOSIT);
		assertThat(r1.status()).isEqualTo(PaymentStatus.PAYMENT_COMPLETED);
	}
}
