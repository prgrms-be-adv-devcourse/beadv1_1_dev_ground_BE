package io.devground.dbay.domain.product.product.infra.kafka;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import io.devground.core.event.image.ImageDeleteEvent;
import io.devground.core.event.image.ImagePushEvent;
import io.devground.core.event.product.ProductImageDeleteEvent;
import io.devground.core.event.product.ProductImagePushEvent;
import io.devground.dbay.domain.product.product.config.ProductTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO: 추후 LogUtil 사용 가능
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ProductTopicProperties productTopicProperties;

	public void publishProductImagePush(ProductImagePushEvent event) {

		String topic = productTopicProperties.getImage().getPush();
		sendKafkaWithEventId(topic, event.referenceCode(), event);
	}

	public void publishProductImageDelete(ProductImageDeleteEvent event) {

		String topic = productTopicProperties.getImage().getDelete();
		sendKafkaWithEventId(topic, event.referenceCode(), event);
	}

	public void publishImagePush(ImagePushEvent event) {

		String topic = productTopicProperties.getImage().getPush();
		sendKafkaWithEventId(topic, event.referenceCode(), event);
	}

	public void publishImageDelete(ImageDeleteEvent event) {

		String topic = productTopicProperties.getImage().getDelete();
		sendKafkaWithEventId(topic, event.referenceCode(), event);
	}

	private void sendKafkaWithEventId(String topic, String key, Object payload) {

		UUID eventId = UUID.randomUUID();

		Message<Object> kafkaMessage = MessageBuilder.withPayload(payload)
			.setHeader(KafkaHeaders.TOPIC, topic)
			.setHeader(KafkaHeaders.KEY, key)
			.setHeader("eventId", eventId.toString())
			.build();

		kafkaTemplate.send(kafkaMessage)
			.whenComplete((result, exception) -> {
				if (exception != null) {
					log.error("Kafka 전송 실패 - Topic: {}, Key: {}, Exception: ", topic, key, exception);
					// TODO: 보상 트랜잭션? 여기서인가? 고려
				} else {
					log.info("Kafka 전송 성공 - Topic: {}, Key: {}", topic, key);
				}
			});
	}
}
