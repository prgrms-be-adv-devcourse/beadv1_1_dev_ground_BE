package io.devground.dbay.order.infrastructure.adapter.in.kafka;

import io.devground.core.commands.order.CompleteOrderCommand;
import io.devground.core.commands.order.NotifyOrderCreateFailedAlertCommand;
import io.devground.dbay.order.domain.port.in.OrderUseCase;
import io.devground.dbay.order.domain.vo.OrderCode;
import io.devground.dbay.order.domain.vo.UserCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@KafkaListener(topics = {
	"${orders.command.topic.purchase}"
})
@RequiredArgsConstructor
public class OrderCommandConsumer {

	private final OrderUseCase orderUseCase;

	// 실패 이벤트
	// 결제에서 실패했을때 주문 상태 취소
	@KafkaHandler
	public void handle(@Payload NotifyOrderCreateFailedAlertCommand command) {
		orderUseCase.cancelOrder(new UserCode(command.userCode()), new OrderCode(command.orderCode()));
	}

	// 예치금 성공했을때 주문 상태 변경
	@KafkaHandler
	public void handle(@Payload CompleteOrderCommand command) {
		orderUseCase.paidOrder(new UserCode(command.userCode()), new OrderCode(command.orderCode()));
	}
}
