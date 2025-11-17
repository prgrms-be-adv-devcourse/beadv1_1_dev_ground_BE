package io.devground.dbay.domain.product.product.infra.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.devground.core.event.product.ProductImagesDeleteEvent;
import io.devground.core.event.product.ProductImagesPushEvent;
import io.devground.dbay.domain.product.product.config.ProductTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "saga")
@Component
@RequiredArgsConstructor
public class ProductKafkaProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ProductTopicProperties productTopicProperties;

	public void publishProductImagePush(ProductImagesPushEvent event) {

		String topic = productTopicProperties.getImage().getPush();
		kafkaTemplate.send(topic, event.sagaId(), event);
	}

	public void publishProductImageDelete(ProductImagesDeleteEvent event) {

		String topic = productTopicProperties.getImage().getDelete();
		kafkaTemplate.send(topic, event.sagaId(), event);
	}
}
