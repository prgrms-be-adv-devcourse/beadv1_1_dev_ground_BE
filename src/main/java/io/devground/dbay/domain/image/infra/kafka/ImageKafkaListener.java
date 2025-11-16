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
import io.devground.dbay.common.saga.entity.Saga;
import io.devground.dbay.common.saga.service.SagaService;
import io.devground.dbay.common.saga.vo.SagaStep;
import io.devground.dbay.domain.image.service.ImageService;
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

	private final ImageService imageService;
	private final ImageKafkaProducer imageKafkaProducer;
	private final SagaService sagaService;

	@KafkaHandler
	public void handleProductImagePush(ProductImagesPushEvent event) {

		String sagaId = event.sagaId();
		String referenceCode = event.referenceCode();
		ImageType imageType = event.imageType();

		Saga saga = sagaService.getSaga(sagaId);
		SagaStep step = saga.getCurrentStep();

		if (step.ordinal() >= SagaStep.IMAGE_DB_SAVE.ordinal()) {
			log.warn("이미 종료된 이미지 등록 Saga - SagaId: {}, ProductCode: {}, Step: {}",
				sagaId, referenceCode, step);

			return;
		}

		String thumbnailUrl = imageService.saveImages(imageType, referenceCode, event.imageUrls());

		log.info("상품 이미지 등록 성공 - SagaId: {}, ProductCode: {}", sagaId, referenceCode);

		imageKafkaProducer.publishImageProcessed(
			new ImageProcessedEvent(sagaId, imageType, referenceCode, EventType.PUSH, thumbnailUrl, true, null));
	}

	@KafkaHandler
	public void handleProductImageDelete(ProductImagesDeleteEvent event) {

		String sagaId = event.sagaId();
		String referenceCode = event.referenceCode();
		ImageType imageType = event.imageType();
		List<String> deleteUrls = event.deleteUrls();

		Saga saga = sagaService.getSaga(sagaId);
		SagaStep step = saga.getCurrentStep();

		if (step.ordinal() >= SagaStep.IMAGE_DELETED.ordinal()) {
			log.warn("이미 종료된 이미지 삭제 Saga - SagaId: {}, ProductCode: {}, Step: {}",
				sagaId, referenceCode, step);

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
			new ImageProcessedEvent(sagaId, imageType, referenceCode, EventType.DELETE, null, true, null));
	}
}
