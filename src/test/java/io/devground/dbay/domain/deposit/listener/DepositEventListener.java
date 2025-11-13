package io.devground.dbay.domain.deposit.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DepositEventListener {

    @KafkaListener(topics = "deposits-events", groupId = "dbay-consumer-group")
    public void listen(String message) {
        System.out.println("💬 [Kafka] Received message: " + message);
    }
}
