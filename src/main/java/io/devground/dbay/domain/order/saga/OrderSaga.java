package io.devground.dbay.domain.order.saga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.commands.cart.DeleteCartItemsCommand;
import io.devground.core.commands.deposit.WithdrawDeposit;
import io.devground.core.commands.order.CompleteOrderCommand;
import io.devground.core.commands.order.NotifyOrderCreateFailedAlertCommand;
import io.devground.core.event.order.OrderCreatedEvent;
import io.devground.core.commands.payment.CancelCreatePaymentCommand;
import io.devground.core.commands.payment.CompletePaymentCommand;
import io.devground.core.commands.payment.PaymentCreateCommand;
import io.devground.core.event.payment.CancelCreatePaymentEvent;
import io.devground.core.event.payment.PaymentCreatedEvent;
import io.devground.core.event.payment.PaymentCreatedFailed;
import io.devground.core.event.deposit.DepositWithdrawFailed;
import io.devground.core.event.deposit.DepositWithdrawnSuccess;
import lombok.RequiredArgsConstructor;

@Component
@KafkaListener(
	topics = {
		"${orders.event.topic.purchase}",
		"${payments.event.topic.purchase}",
		"${deposits.event.topic.purchase}",
	}
)
@RequiredArgsConstructor
public class OrderSaga {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${orders.command.topic.purchase}")
	private String ordersCommandTopicName;

	@Value("${payments.command.topic.purchase}")
	private String paymentsCommandTopicName;

	@Value("${deposits.command.topic.purchase}")
	private String depositsCommandTopicName;

	@Value("${carts.command.topic.purchase}")
	private String cartsCommandTopicName;

	// 이벤트 시작점 주문 -> 결제
	@KafkaHandler
	public void handleEvent(@Payload OrderCreatedEvent event) {
		PaymentCreateCommand paymentCreatedCommand = new PaymentCreateCommand(
			event.userCode(),
			event.orderCode(),
			event.totalAmount(),
			event.productCodes()
		);

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
			event.orderCode(), event.productCodes());

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

		// 장바구니 삭제 이벤트 전송
		DeleteCartItemsCommand deleteCartItemsCommand = new DeleteCartItemsCommand(event.userCode(),
			event.productCodes());

		kafkaTemplate.send(cartsCommandTopicName, event.orderCode(), deleteCartItemsCommand);
	}
}
