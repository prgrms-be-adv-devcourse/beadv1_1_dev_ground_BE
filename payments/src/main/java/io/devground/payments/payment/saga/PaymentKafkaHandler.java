package io.devground.payments.payment.saga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.commands.deposit.RefundDeposit;
import io.devground.core.commands.payment.CancelCreatePaymentCommand;
import io.devground.core.commands.payment.CompletePaymentCommand;
import io.devground.core.commands.payment.DepositRefundCommand;
import io.devground.core.commands.payment.PaymentCreateCommand;
import io.devground.core.event.deposit.DepositChargeFailed;
import io.devground.core.event.deposit.DepositChargedSuccess;
import io.devground.core.model.vo.DepositHistoryType;

import io.devground.payments.payment.model.dto.request.RefundRequest;
import io.devground.payments.payment.model.dto.request.TossRefundRequest;
import io.devground.payments.payment.model.vo.PaymentConfirmRequest;
import io.devground.payments.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@KafkaListener(
	topics = {
		"${payments.command.topic.purchase}",
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

	@Value("${payments.event.topic.purchase}")
	private String paymentPurchaseEventTopic;

	//결제 가능 -> 주문으로 결제 성공으로 이벤트
	@KafkaHandler
	public void handleEvent(@Payload PaymentCreateCommand command) {
		PaymentConfirmRequest request = new PaymentConfirmRequest(command.orderCode(), true, command.totalAmount(), null, command.productCodes());
		paymentService.process(command.userCode(), request);

	}

	//결제 최종 성공
	@KafkaHandler
	public void handleEvent(@Payload CompletePaymentCommand completePaymentCommand) {
		paymentService.applyDepositPayment(completePaymentCommand.orderCode());
	}

	//결제 실패
	@KafkaHandler
	public void handleEvent(@Payload CancelCreatePaymentCommand command) {
		paymentService.cancelDepositPayment(command.orderCode());
	}

	//예치금 환불(결제 취소)
	@KafkaHandler
	public void handleEvent(@Payload DepositRefundCommand command) {
		RefundRequest request = new RefundRequest(command.userCode(), command.orderCode(), command.amount());
		paymentService.refund(request);

		RefundDeposit refundDeposit = new RefundDeposit(command.userCode(), command.amount(), DepositHistoryType.REFUND_INTERNAL);
		kafkaTemplate.send(depositsCommandTopic, refundDeposit);
	}

	//예치금 충전 성공
	@KafkaHandler
	public void handleEvent(@Payload DepositChargedSuccess depositChargedSuccessEvent) {
		paymentService.applyDepositCharge(depositChargedSuccessEvent.userCode());
	}

	//예치금 충전 실패
	@KafkaHandler
	public void handleEvent(@Payload DepositChargeFailed depositChargeFailed) {
		TossRefundRequest request = new TossRefundRequest(depositChargeFailed.userCode(), depositChargeFailed.paymentKey(), depositChargeFailed.amount());
		paymentService.tossRefund(request);
	}
}
