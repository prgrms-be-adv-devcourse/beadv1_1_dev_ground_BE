package io.devground.dbay.order.infrastructure.adapter.out.event;

import io.devground.core.event.order.OrderCreatedEvent;
import io.devground.dbay.order.application.port.out.kafka.OrderKafkaEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderKafkaEventPort orderEventPort;

    @Value("${orders.event.topic.purchase}")
    private String orderEventTopicName;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("이벤트객체2: {}", event);
        orderEventPort.publishOrderCreated(event.userCode(), event.orderCode(), event.totalAmount(), event.productCodes());
    }
}
