package io.devground.product.product.infrastructure.adapter.in.kafka;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.commands.product.ProductSoldCommand;
import io.devground.core.event.image.ImageProcessedEvent;
import io.devground.product.product.application.port.out.ProductOrchestrationPort;
import io.devground.product.product.domain.port.in.ProductUseCase;
import io.devground.product.product.domain.vo.request.CartProductsDto;
import io.devground.product.product.infrastructure.saga.entity.Saga;
import io.devground.product.product.infrastructure.saga.service.SagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "saga")
@Component
@Transactional
@RequiredArgsConstructor
@KafkaListener(
	topics = {
			"${images.topic.processed}",
			"${products.topic.purchase.sold}"
	}
)
public class ProductKafkaListener {

	private final ProductOrchestrationPort orchestrator;
	private final ProductUseCase productApplication;
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

			if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
				productApplication.updateThumbnail(event.referenceCode(), thumbnailUrl);
			}

			orchestrator.handleImageProcessSuccess(sagaId, event);
		} else {
			orchestrator.handleImageProcessFailure(sagaId, event);
		}
	}

	@KafkaHandler
	public void handleProductSold(@Payload ProductSoldCommand command) {
		productApplication.updateStatusToSoldByOrder(new CartProductsDto(command.productCodes()));
	}
}
