package io.devground.image.infrastructure.adapter.in.kafka;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
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
import io.devground.image.application.service.ImageApplicationService;
import io.devground.image.infrastructure.adapter.out.ImageInboxJpaRepository;
import io.devground.image.infrastructure.adapter.out.kafka.ImageKafkaProducer;
import io.devground.image.infrastructure.model.persistence.ImageInbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "saga")
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

	private final ImageApplicationService imageService;
	private final ImageKafkaProducer imageKafkaProducer;
	private final ImageInboxJpaRepository imageInboxRepository;

	@KafkaHandler
	public void handleProductImagePush(ProductImagesPushEvent event) {

		String sagaId = event.sagaId();
		String referenceCode = event.referenceCode();
		ImageType imageType = event.imageType();

		log.info("상품 이미지 등록 시도 - SagaId: {}, ProductCode: {}", sagaId, referenceCode);

		try {
			imageInboxRepository.save(ImageInbox.builder()
				.sagaId(sagaId)
				.eventType(EventType.PUSH)
				.referenceCode(referenceCode)
				.build()
			);
		} catch (DataIntegrityViolationException e) {
			log.warn("중복 이미지 등록 명령 - SagaId: {}, ProductCode: {}", sagaId, referenceCode);

			return;
		}

		String thumbnailUrl = imageService.saveImages(imageType, referenceCode, event.imageUrls());

		log.info("상품 이미지 등록 성공 - SagaId: {}, ProductCode: {}", sagaId, referenceCode);

		imageKafkaProducer.publishImageProcessed(
			new ImageProcessedEvent(
				sagaId, imageType, referenceCode, EventType.PUSH, event.imageUrls(), thumbnailUrl, true, null
			)
		);
	}

	@KafkaHandler
	public void handleProductImageDelete(ProductImagesDeleteEvent event) {

		String sagaId = event.sagaId();
		String referenceCode = event.referenceCode();
		ImageType imageType = event.imageType();
		List<String> deleteUrls = event.deleteUrls();

		try {
			imageInboxRepository.save(ImageInbox.builder()
				.sagaId(sagaId)
				.eventType(EventType.DELETE)
				.referenceCode(referenceCode)
				.build());
		} catch (DataIntegrityViolationException e) {
			log.warn("중복 이미지 삭제 명령 - SagaId: {}, ProductCode: {}", sagaId, referenceCode);

			return;
		}

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
			new ImageProcessedEvent(
				sagaId, imageType, referenceCode, EventType.DELETE, event.deleteUrls(), null, true, null
			)
		);
	}
}
