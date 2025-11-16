package io.devground.dbay.domain.product.product.infra.kafka;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.event.image.ImageProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "saga")
@Component
@Transactional
@RequiredArgsConstructor
@KafkaListener(
	topics = {
		"${images.topic.processed-dlt}"
	}
)
public class ProductKafkaDltListener {

	private final ProductImageSagaOrchestrator orchestrator;

	@KafkaHandler
	public void handleImageProcessDlt(ImageProcessedEvent event) {

		String sagaId = event.sagaId();

		// 외부에서 직접 Image API 호출한 경우
		if (sagaId == null) {
			log.info("Saga 추적 종료 - SagaId가 없는 이벤트");

			return;
		}

		log.error("이미지 처리 결과 수신 재시도 모두 실패 - SagaId: {}, ProductCode: {}: ", sagaId, event.referenceCode());

		ImageProcessedEvent failureEvent = new ImageProcessedEvent(
			sagaId, event.imageType(), event.referenceCode(), event.eventType(), null, false, "이미지 처리 결과 수신 재시도 모두 실패"
		);

		orchestrator.handleImageProcessFailure(sagaId, failureEvent);
	}
}
