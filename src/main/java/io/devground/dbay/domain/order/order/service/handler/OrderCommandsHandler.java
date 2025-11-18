package io.devground.dbay.domain.order.order.service.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.commands.order.CompleteOrderCommand;
import io.devground.core.commands.order.NotifyOrderCreateFailedAlertCommand;
import io.devground.dbay.domain.order.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@KafkaListener(topics = {
	"${orders.command.topic.purchase}"
})
@RequiredArgsConstructor
public class OrderCommandsHandler {

	private final OrderService orderService;

	private final KafkaTemplate<String, Object> kafkaTemplate;

	// 실패 이벤트
	// 결제에서 실패했을때 주문 상태 취소
	@KafkaHandler
	public void handle(@Payload NotifyOrderCreateFailedAlertCommand command) {
		orderService.cancelOrder(command.userCode() ,command.orderCode());
	}

	// 예치금 성공했을때 주문 상태 변경
	@KafkaHandler
	public void handle(@Payload CompleteOrderCommand command) {
		orderService.paidOrder(command.userCode(), command.orderCode());
	}
}
