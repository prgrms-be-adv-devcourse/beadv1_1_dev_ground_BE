package io.devground.dbay.domain.payment.saga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.commands.deposit.ChargeDeposit;
import io.devground.core.commands.payment.NotifyDepositChargeFailedAlertCommand;
import io.devground.core.commands.payment.NotifyDepositChargeSuccessAlertCommand;
import io.devground.core.commands.payment.PaymentChargeDepositCommand;
import io.devground.core.event.order.Temp.command.CancelCreatePaymentCommand;
import io.devground.core.event.order.Temp.command.CompletePaymentCommand;
import io.devground.core.event.order.Temp.command.PaymentCreateCommand;
import io.devground.core.event.order.Temp.event.CancelCreatePaymentEvent;
import io.devground.core.event.order.Temp.event.PaymentCreatedEvent;
import io.devground.core.event.order.Temp.event.PaymentCreatedFailed;
import io.devground.core.events.deposit.DepositChargeFailed;
import io.devground.core.events.deposit.DepositChargedSuccess;
import io.devground.core.model.vo.DepositHistoryType;
import io.devground.dbay.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@KafkaListener(
	topics = {

		"${payments.command.topic.name}",
		"${payments.event.topic.name}",
		"${deposits.event.topic.name}"
	}
)
@RequiredArgsConstructor
public class PaymentKafkaHandler {

	private final PaymentService paymentService;

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${payments.command.topic.purchase}")
	private String paymentOrderCommandTopic;

	@Value("${deposits.command.topic.name}")
	private String depositsCommandTopic;

	//결제 가능 -> 주문으로 결제 성공으로 이벤트
	@KafkaHandler
	public void handleEvent(@Payload PaymentCreateCommand command) {
		String paymentKey = "";
		boolean canPay = paymentService.pay(
			command.userCode(),
			command.orderCode(),
			command.totalAmount(),
			paymentKey
		);

		if (canPay) {
			PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent(command.userCode(), command.totalAmount(),
				DepositHistoryType.PAYMENT_INTERNAL, command.orderCode(), command.productCodes());
			kafkaTemplate.send(paymentOrderCommandTopic, paymentCreatedEvent);
		} else {
			log.error("결제에 실패했습니다.");
			PaymentCreatedFailed paymentCreatedFailed = new PaymentCreatedFailed(command.orderCode(),
				command.userCode(), "결제에 실패했습니다.");
			kafkaTemplate.send(paymentOrderCommandTopic, paymentCreatedFailed);
		}

	}

	//결제 최종 성공
	@KafkaHandler
	public void handleEvent(@Payload CompletePaymentCommand completePaymentCommand) {
		paymentService.applyDepositPayment(completePaymentCommand.orderCode());
	}

	//결제 취소
	@KafkaHandler
	public void handleEvent(@Payload CancelCreatePaymentCommand command) {
		CancelCreatePaymentEvent cancelCreatePaymentEvent = new CancelCreatePaymentEvent(command.userCode(),
			command.orderCode(), "결제가 취소되었습니다.");

		paymentService.canceledDepositPayment(command.orderCode());

		kafkaTemplate.send(paymentOrderCommandTopic, cancelCreatePaymentEvent);
	}

	//예치금 충전
	@KafkaHandler
	public void handleEvent(@Payload PaymentChargeDepositCommand command) {
		ChargeDeposit chargeDeposit = new ChargeDeposit(command.userCode(), command.totalAmount(),
			DepositHistoryType.CHARGE_TOSS);

		kafkaTemplate.send(depositsCommandTopic, chargeDeposit);
	}

	//예치금 충전 성공
	@KafkaHandler
	public void handleEvent(@Payload DepositChargedSuccess depositChargedSuccessEvent) {
		log.info("예치금 충전 성공: userCode={}, amount={}", depositChargedSuccessEvent.userCode(),
			depositChargedSuccessEvent.amount());
		NotifyDepositChargeSuccessAlertCommand command = new NotifyDepositChargeSuccessAlertCommand(
			depositChargedSuccessEvent.userCode(), depositChargedSuccessEvent.amount());

		kafkaTemplate.send(paymentOrderCommandTopic, command);
	}

	//예치금 충전 실패
	@KafkaHandler
	public void handleEvent(@Payload DepositChargeFailed depositChargeFailed) {
		paymentService.refund(depositChargeFailed.userCode(), depositChargeFailed.amount());

		log.info("예치금 충전 실패: userCode={}, amount={}", depositChargeFailed.userCode(), depositChargeFailed.amount());

		NotifyDepositChargeFailedAlertCommand command = new NotifyDepositChargeFailedAlertCommand(
			depositChargeFailed.userCode(), "예치금 충전에 실패했습니다.");

		kafkaTemplate.send(paymentOrderCommandTopic, command);
	}
}
