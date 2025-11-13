package io.devground.dbay.domain.product.product.infra.kafka;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.event.image.ImageProcessedEvent;
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

	@KafkaHandler
	public void handleImageProcessed(ImageProcessedEvent event) {

		String sagaId = event.sagaId();

		log.info("이미지 처리 결과 수신 - SagaId: {}, referenceCode: {}, isSuccess: {}",
			sagaId, event.referenceCode(), event.isSuccess());

		// 외부에서 직접 Image API 호출한 경우
		if (sagaId == null) {
			log.info("Saga 추적 종료 - SagaId가 없는 이벤트");
			return;
		}

		try {
			if (event.isSuccess()) {
				orchestrator.handleImageProcessSuccess(sagaId, event);
			} else {
				orchestrator.handleImageProcessFailure(sagaId, event);
			}
		} catch (Exception e) {
			log.error("Saga 처리 중 오류 발생 - SagaId: {}, Exception: ", sagaId, e);

			throw e;
		}
	}
}
