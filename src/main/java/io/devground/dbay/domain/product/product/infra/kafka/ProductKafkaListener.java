package io.devground.dbay.domain.product.product.infra.kafka;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.event.image.ImageProcessedEvent;
import io.devground.dbay.common.saga.entity.Saga;
import io.devground.dbay.common.saga.service.SagaService;
import io.devground.dbay.domain.product.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
@KafkaListener(
	topics = "${images.topic.processed}"
)
public class ProductKafkaListener {

	private final ProductImageSagaOrchestrator orchestrator;
	private final ProductService productService;
	private final SagaService sagaService;

	@KafkaHandler
	public void handleImageProcessed(ImageProcessedEvent event) {

		String sagaId = event.sagaId();

		log.info("이미지 처리 결과 수신 - SagaId: {}, ProductCode: {}, isSuccess: {}",
			sagaId, event.referenceCode(), event.isSuccess());

		// 외부에서 직접 Image API 호출한 경우
		if (sagaId == null) {
			log.info("Saga 추적 종료 - SagaId가 없는 이벤트");
			return;
		}

		Saga saga = sagaService.getSaga(sagaId);

		if (saga.getSagaStatus().isTerminal()) {
			log.warn("이미 종료된 Saga - SagaId: {}, ProductCode: {}, Step: {}",
				sagaId, event.referenceCode(), saga.getCurrentStep());

			return;
		}

		if (event.isSuccess()) {
			String thumbnailUrl = event.thumbnailUrl();

			if (thumbnailUrl != null) {
				productService.getProductByCode(event.referenceCode()).updateThumbnail(thumbnailUrl);
			}

			orchestrator.handleImageProcessSuccess(sagaId, event);
		} else {
			orchestrator.handleImageProcessFailure(sagaId, event);
		}
	}
}
