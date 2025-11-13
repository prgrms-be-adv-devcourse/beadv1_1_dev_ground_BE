package io.devground.dbay.domain.order.saga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.commands.order.CompleteOrderCommand;
import io.devground.core.commands.order.NotifyOrderCreateFailedAlertCommand;
import io.devground.core.commands.deposit.WithdrawDeposit;
import io.devground.core.event.order.OrderCreatedEvent;
import io.devground.core.event.order.Temp.command.CancelCreatePaymentCommand;
import io.devground.core.event.order.Temp.command.CompletePaymentCommand;
import io.devground.core.event.order.Temp.command.PaymentCreateCommand;
import io.devground.core.event.order.Temp.event.CancelCreatePaymentEvent;
import io.devground.core.event.order.Temp.event.PaymentCreatedEvent;
import io.devground.core.event.order.Temp.event.PaymentCreatedFailed;
import io.devground.core.events.deposit.DepositWithdrawFailed;
import io.devground.core.events.deposit.DepositWithdrawnSuccess;
import io.devground.dbay.domain.order.order.service.OrderService;
import lombok.RequiredArgsConstructor;

@Component
@KafkaListener(
	topics = {
		"${orders.event.topic.name}",
		"${payments.command.topic.name}",
		"${deposits.command.topic.name}",
	}
)
@RequiredArgsConstructor
public class OrderSaga {

	private final OrderService orderService;

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${orders.command.topic.name}")
	private String ordersCommandTopicName;

	@Value("${payments.command.topic.name}")
	private String paymentsCommandTopicName;

	@Value("${deposits.command.topic.name}")
	private String depositsCommandTopicName;

	// 이벤트 시작점 주문 -> 결제
	@KafkaHandler
	public void handleEvent(@Payload OrderCreatedEvent event) {
		PaymentCreateCommand paymentCreatedCommand = new PaymentCreateCommand(event.userCode(), event.orderCode(),
			event.totalAmount());

		kafkaTemplate.send(paymentsCommandTopicName, event.orderCode(), paymentCreatedCommand);
	}

	// 결제에서 실패했을때 결제 -> 주문
	@KafkaHandler
	public void handleEvent(@Payload PaymentCreatedFailed event) {
		NotifyOrderCreateFailedAlertCommand notifyOrderCreateFailedAlertCommand = new NotifyOrderCreateFailedAlertCommand(
			event.userCode(), event.orderCode(), "결제 요청에 실패하였습니다.");

		kafkaTemplate.send(ordersCommandTopicName, event.orderCode(), notifyOrderCreateFailedAlertCommand);
	}

	// 결제 성공했을때 결제 -> 예치금
	@KafkaHandler
	public void handleEvent(@Payload PaymentCreatedEvent event) {
		WithdrawDeposit withdrawDepositCommand = new WithdrawDeposit(event.userCode(), event.amount(), event.type(),
			event.orderCode());

		kafkaTemplate.send(depositsCommandTopicName, event.orderCode(), withdrawDepositCommand);
	}

	// 예치금에서 실패했을때 예치금 -> 결제
	@KafkaHandler
	public void handleEvent(@Payload DepositWithdrawFailed event) {
		CancelCreatePaymentCommand cancelCreatePaymentCommand = new CancelCreatePaymentCommand(event.userCode(),
			event.orderCode(), event.msg());

		kafkaTemplate.send(paymentsCommandTopicName, event.orderCode(), cancelCreatePaymentCommand);
	}

	// 예치금에서 실패했을때 결제 -> 주문
	@KafkaHandler
	public void handleEvent(@Payload CancelCreatePaymentEvent event) {
		NotifyOrderCreateFailedAlertCommand notifyOrderCreateFailedAlertCommand = new NotifyOrderCreateFailedAlertCommand(
			event.userCode(), event.orderCode(), event.msg());

		kafkaTemplate.send(ordersCommandTopicName, event.orderCode(), notifyOrderCreateFailedAlertCommand);
	}

	// 예치금 성공 -> 결제 상태 변경, 주문 상태 변경
	@KafkaHandler
	public void handleEvent(@Payload DepositWithdrawnSuccess event) {
		// 결제 상태 변경 이벤트 전송
		CompletePaymentCommand completePaymentCommand = new CompletePaymentCommand(event.orderCode());

		kafkaTemplate.send(paymentsCommandTopicName, event.orderCode(), completePaymentCommand);

		// 주문 상태 변경 이벤트 전송
		CompleteOrderCommand completeOrderCommand = new CompleteOrderCommand(event.userCode(), event.orderCode());

		kafkaTemplate.send(ordersCommandTopicName, event.orderCode(), completeOrderCommand);
	}
}
