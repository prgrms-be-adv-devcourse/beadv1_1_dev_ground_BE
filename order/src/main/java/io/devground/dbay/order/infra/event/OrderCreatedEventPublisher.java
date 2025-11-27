package io.devground.dbay.order.infra.event;

import io.devground.core.event.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${orders.event.topic.purchase}")
	private String orderEventTopicName;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onOrderCreated(OrderCreatedEvent event) {
		kafkaTemplate.send(orderEventTopicName, event.orderCode(), event);
	}
}
