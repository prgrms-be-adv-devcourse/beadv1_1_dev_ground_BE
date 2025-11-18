package io.devground.dbay.domain.image.infra.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.devground.core.event.image.ImageProcessedEvent;
import io.devground.dbay.domain.image.config.ImageTopicProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageKafkaProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ImageTopicProperties imageTopicProperties;

	public void publishImageProcessed(ImageProcessedEvent event) {

		String topic = imageTopicProperties.getProcessed();
		kafkaTemplate.send(topic, event.sagaId(), event);
	}

}
