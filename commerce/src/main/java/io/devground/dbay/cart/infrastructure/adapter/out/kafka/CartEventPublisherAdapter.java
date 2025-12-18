package io.devground.dbay.cart.infrastructure.adapter.out.kafka;

import io.devground.core.event.cart.CartCreatedEvent;
import io.devground.core.event.cart.CartCreatedFailedEvent;
import io.devground.core.event.cart.CartDeletedEvent;
import io.devground.core.event.cart.CartDeletedFailedEvent;
import io.devground.dbay.cart.application.port.out.kafka.CartEventPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartEventPublisherAdapter implements CartEventPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${carts.event.topic.join}")
    private String cartsJoinUserEventTopicName;

    @Value("${carts.event.topic.purchase}")
    private String cartsOrderEventTopicName;

    @Override
    public void publishCartCreated(String userCode, String cartCode) {
        kafkaTemplate.send(cartsJoinUserEventTopicName, userCode, new CartCreatedEvent(userCode, cartCode));
    }

    @Override
    public void publishCartCreatedFailed(String userCode, String msg) {
        kafkaTemplate.send(cartsJoinUserEventTopicName, userCode, new CartCreatedFailedEvent(userCode, msg));
    }

    @Override
    public void publishCartDeleted(String userCode) {
        kafkaTemplate.send(cartsOrderEventTopicName, userCode, new CartDeletedEvent(userCode));
    }

    @Override
    public void publishCartDeletedFailed(String userCode, String msg) {
        kafkaTemplate.send(cartsOrderEventTopicName, userCode, new CartDeletedFailedEvent(userCode, msg));
    }

    @Override
    public void publishOrderCompleted(List<String> productCodes) {
        kafkaTemplate.send(cartsOrderEventTopicName, productCodes);
    }
}
