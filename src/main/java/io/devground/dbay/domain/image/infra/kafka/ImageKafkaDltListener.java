package io.devground.dbay.domain.image.infra.kafka;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.event.image.ImageProcessedEvent;
import io.devground.core.event.product.ProductImagesDeleteEvent;
import io.devground.core.event.product.ProductImagesPushEvent;
import io.devground.core.event.vo.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
@KafkaListener(
	topics = {
		"${products.topic.image.push-dlt}",
		"${products.topic.image.delete-dlt}"
	}
)
public class ImageKafkaDltListener {

	private final ImageKafkaProducer imageKafkaProducer;

	@KafkaHandler
	public void handleProductImagePushDlt(ProductImagesPushEvent event) {

		log.error("상품 이미지 등록 재시도 모두 실패 - SagaId: {}, ProductCode: {}", event.sagaId(), event.referenceCode());

		imageKafkaProducer.publishImageProcessed(
			new ImageProcessedEvent(
				event.sagaId(), event.imageType(), event.referenceCode(), EventType.PUSH, null, false,
				"상품 이미지 등록 재시도 모두 실패"
			)
		);
	}

	@KafkaHandler
	public void handleProductImageDeleteDlt(ProductImagesDeleteEvent event) {

		log.error("상품 이미지 삭제 재시도 모두 실패 - SagaId: {}, ProductCode: {}", event.sagaId(), event.referenceCode());

		imageKafkaProducer.publishImageProcessed(
			new ImageProcessedEvent(
				event.sagaId(), event.imageType(), event.referenceCode(), EventType.PUSH, null, false,
				"상품 이미지 삭제 재시도 모두 실패"
			)
		);
	}
}
