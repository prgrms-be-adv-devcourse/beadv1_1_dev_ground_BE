package io.devground.dbay.domain.image.infra.kafka;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.event.image.ImageProcessedEvent;
import io.devground.core.event.product.ProductImagesDeleteEvent;
import io.devground.core.event.product.ProductImagesPushEvent;
import io.devground.core.event.vo.EventType;
import io.devground.dbay.domain.image.entity.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "saga")
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

		try {
			imageKafkaProducer.publishImageProcessed(
				new ImageProcessedEvent(
					event.sagaId(), event.imageType(), event.referenceCode(), EventType.PUSH, event.imageUrls(),
					null, false, "상품 이미지 등록 재시도 모두 실패"
				)
			);
		} catch (Exception e) {
			log.error("상품 이미지 등록 DLT 처리도 실패/수동 정리 필요 - SagaId: {}, ProductCode: {}",
				event.sagaId(), event.referenceCode()
			);
		}
	}

	@KafkaHandler
	public void handleProductImageDeleteDlt(ProductImagesDeleteEvent event) {

		log.error("상품 이미지 삭제 재시도 모두 실패 - SagaId: {}, ProductCode: {}", event.sagaId(), event.referenceCode());

		try {
			imageKafkaProducer.publishImageProcessed(
				new ImageProcessedEvent(
					event.sagaId(), event.imageType(), event.referenceCode(), EventType.DELETE, null, null, false,
					"상품 이미지 삭제 재시도 모두 실패"
				)
			);
		} catch (Exception e) {
			log.error("상품 이미지 삭제 DLT 처리도 실패/수동 정리 필요 - SagaId: {}, ProductCode: {}",
				event.sagaId(), event.referenceCode()
			);
		}
	}
}
