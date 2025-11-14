package io.devground.dbay.domain.image.infra.kafka;

import java.util.List;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.devground.core.event.image.ImageProcessedEvent;
import io.devground.core.event.product.ProductImagesDeleteEvent;
import io.devground.core.event.product.ProductImagesPushEvent;
import io.devground.core.event.vo.EventType;
import io.devground.core.model.vo.ImageType;
import io.devground.dbay.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
@KafkaListener(
	topics = {
		"${products.topic.image.push}",
		"${products.topic.image.delete}"
	}
)
public class ImageKafkaListener {

	private final ImageService imageService;
	private final ImageKafkaProducer imageKafkaProducer;

	@KafkaHandler
	public void handleProductImagePush(ProductImagesPushEvent event) {

		String sagaId = event.sagaId();
		String referenceCode = event.referenceCode();
		ImageType imageType = event.imageType();

		try {
			imageService.saveImages(event.imageType(), event.referenceCode(), event.imageUrls());

			log.error("상품 이미지 등록 성공 - SagaId: {}, ProductCode: {}", event.sagaId(), event.referenceCode());

			imageKafkaProducer.publishImageProcessed(
				new ImageProcessedEvent(sagaId, imageType, referenceCode, EventType.PUSH, true, null));

		} catch (Exception e) {
			log.error(
				"상품 이미지 등록 실패 - SagaId: {}, ProductCode: {}, Exception: ", event.sagaId(), event.referenceCode(), e
			);

			imageKafkaProducer.publishImageProcessed(
				new ImageProcessedEvent(sagaId, imageType, referenceCode, EventType.PUSH, false, e.getMessage()));
		}
	}

	@KafkaHandler
	public void handleProductImageDelete(ProductImagesDeleteEvent event) {

		String sagaId = event.sagaId();
		String referenceCode = event.referenceCode();
		ImageType imageType = event.imageType();
		List<String> deleteUrls = event.deleteUrls();

		try {
			if (CollectionUtils.isEmpty(deleteUrls)) {
				imageService.deleteImageByReferences(imageType, referenceCode);

				log.info("상품 전체 이미지 삭제 완료 - SagaId: {}, ProductCode: {}", sagaId, referenceCode);
			} else {
				imageService.deleteImagesByReferencesAndUrls(
					imageType, referenceCode, deleteUrls
				);

				log.info("선택된 상품 이미지 삭제 완료 - SagaId: {}, ProductCode: {}, size: {}",
					sagaId, referenceCode, deleteUrls.size());
			}

			imageKafkaProducer.publishImageProcessed(
				new ImageProcessedEvent(sagaId, imageType, referenceCode, EventType.DELETE, true, null));
		} catch (Exception e) {
			log.error("상품 이미지 삭제 실패 - SagaId: {}, ProductCode: {}, Exception: ", sagaId, referenceCode, e);

			imageKafkaProducer.publishImageProcessed(
				new ImageProcessedEvent(sagaId, imageType, referenceCode, EventType.DELETE, false, e.getMessage()));
		}
	}
}
