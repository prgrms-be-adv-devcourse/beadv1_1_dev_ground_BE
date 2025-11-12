package io.devground.dbay.domain.image.infra.kafka;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.devground.core.event.product.ProductImageDeleteEvent;
import io.devground.core.event.product.ProductImagePushEvent;
import io.devground.dbay.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
@KafkaListener(
	topics = "${products.topic.image.push}"
)
public class ImageKafkaListener {

	private final ImageService imageService;

	@KafkaHandler
	public void handleProductImagePush(ProductImagePushEvent event) {

		try {
			imageService.saveImages(event.imageType(), event.referenceCode(), event.imageUrls());
			log.info("성공 Code:{} - DB에 이미지 저장 완료", event.referenceCode());
		} catch (Exception e) {
			// TODO: 분산 트랜잭션 처리?
			log.error("실패 Code:{} - DB에 이미지 저장 실패", event.referenceCode());
			throw e;
		}
	}

	@KafkaHandler
	public void handleProductImageDelete(ProductImageDeleteEvent event) {

		try {
			if (CollectionUtils.isEmpty(event.deleteUrls())) {
				imageService.deleteImageByReferences(event.imageType(), event.referenceCode());
			} else {
				imageService.deleteImagesByReferencesAndUrls(
					event.imageType(),
					event.referenceCode(),
					event.deleteUrls());
			}
			log.info("성공 Code:{} - DB에 이미지 저장 완료", event.referenceCode());
		} catch (Exception e) {
			// TODO: 분산 트랜잭션 처리?
			log.error("실패 Code:{} - DB에 이미지 저장 실패", event.referenceCode());
			throw e;
		}
	}
}
