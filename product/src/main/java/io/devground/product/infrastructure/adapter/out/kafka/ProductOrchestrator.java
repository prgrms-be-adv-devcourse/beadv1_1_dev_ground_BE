package io.devground.product.infrastructure.adapter.out.kafka;

import static io.devground.core.model.vo.ImageType.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.devground.core.event.image.ImageProcessedEvent;
import io.devground.core.event.product.ProductImagesDeleteEvent;
import io.devground.core.event.product.ProductImagesPushEvent;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.product.application.port.out.ImageClientPort;
import io.devground.product.application.port.out.ProductOrchestrationPort;
import io.devground.product.domain.exception.DomainException;
import io.devground.product.infrastructure.model.web.request.ImageUpdatePlan;
import io.devground.product.infrastructure.saga.entity.Saga;
import io.devground.product.infrastructure.saga.service.SagaService;
import io.devground.product.infrastructure.saga.vo.SagaStep;
import io.devground.product.infrastructure.saga.vo.SagaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "saga")
@Component
@RequiredArgsConstructor
public class ProductOrchestrator implements ProductOrchestrationPort {

	private final ImageClientPort imagePort;
	private final SagaService sagaService;
	private final ProductKafkaProducer productKafkaProducer;
	private final ProductImageCompensationService compensationService;

	@Override
	public void uploadProductImages(
		String sellerCode, String productSellerCode, String productCode, List<String> urls
	) {

		if (!productSellerCode.equals(sellerCode)) {
			ErrorCode.IS_NOT_PRODUCT_OWNER.throwServiceException();
		}

		if (CollectionUtils.isEmpty(urls)) {
			return;
		}

		Saga saga = sagaService.findLatestSagaByReferenceCode(productCode);
		String sagaId = saga.getSagaId();

		MDC.put("sagaId", sagaId);
		MDC.put("productCode", productCode);

		log.info("이미지 등록 이벤트 진행");

		try {
			sagaService.updateStep(sagaId, SagaStep.IMAGE_KAFKA_PUBLISHED);

			productKafkaProducer.publishProductImagePush(
				new ProductImagesPushEvent(sagaId, PRODUCT, productCode, urls)
			);

			log.info("이미지 등록 이벤트 발행 완료");
		} catch (DomainException | ServiceException e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("이미지 등록 이벤트 발행 실패");

			compensationService.compensateProductImageUploadFailure(
				sagaId, productCode, "이벤트 발행 실패: " + e.getMessage(), urls
			);

			throw e;
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("이미지 등록 이벤트 발행 실패 - ExStack: ", e);

			compensationService.compensateProductImageUploadFailure(
				sagaId, productCode, "이벤트 발행 실패: " + e.getMessage(), urls
			);

			throw e;
		} finally {
			MDC.clear();
		}
	}

	@Override
	public List<URL> updateProductImages(String productCode, List<String> deleteUrls, List<String> newExtensions) {

		String sagaId = sagaService.startSaga(productCode, SagaType.PRODUCT_IMAGE_UPDATE);

		MDC.put("sagaId", sagaId);
		MDC.put("productCode", productCode);

		log.info("상품 이미지 수정 시도");

		try {
			List<URL> updatedPresignedUrls = new ArrayList<>();

			if (!CollectionUtils.isEmpty(deleteUrls) || !CollectionUtils.isEmpty(newExtensions)) {
				updatedPresignedUrls = imagePort.updateImages
					(
						new ImageUpdatePlan(PRODUCT, productCode, deleteUrls, newExtensions)
					);
			}

			return updatedPresignedUrls;
		} catch (DomainException | ServiceException e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("상품 이미지 수정 실패");

			compensationService.compensateProductImageUpdateFailure(sagaId, productCode, deleteUrls,
				e.getMessage());

			throw e;
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("상품 이미지 수정 실패 - ExStack: ", e);

			compensationService.compensateProductImageUpdateFailure(sagaId, productCode, deleteUrls, e.getMessage());

			throw e;
		} finally {
			MDC.clear();
		}
	}

	@Override
	public void deleteProductImages(String productCode) {

		String sagaId = sagaService.startSaga(productCode, SagaType.PRODUCT_IMAGE_DELETE);

		MDC.put("sagaId", sagaId);
		MDC.put("productCode", productCode);

		log.info("상품 이미지 삭제 시도");

		try {
			sagaService.updateStep(sagaId, SagaStep.IMAGE_KAFKA_PUBLISHED);

			productKafkaProducer.publishProductImageDelete(
				new ProductImagesDeleteEvent(sagaId, PRODUCT, productCode, null)
			);

			log.info("이미지 삭제 이벤트 발행 완료");
		} catch (DomainException | ServiceException e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("이미지 삭제 이벤트 발행 실패");

			sagaService.updateToFail(sagaId, "이미지 삭제 이벤트 발행 실패" + e.getMessage());

			throw e;
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("이미지 삭제 이벤트 발행 실패 - ExStack: ", e);

			sagaService.updateToFail(sagaId, "이미지 삭제 이벤트 발행 실패" + e.getMessage());

			throw e;
		} finally {
			MDC.clear();
		}
	}

	@Override
	public void handleImageProcessSuccess(String sagaId, ImageProcessedEvent event) {

		Saga saga = sagaService.getSaga(sagaId);

		if (saga.getSagaStatus().isTerminal()) {
			log.warn("이미 처리된 성공 Saga");

			return;
		}

		switch (event.eventType()) {
			case PUSH -> sagaService.updateStep(sagaId, SagaStep.IMAGE_DB_SAVE);
			case DELETE -> sagaService.updateStep(sagaId, SagaStep.IMAGE_DELETED);
		}

		sagaService.updateToSuccess(sagaId);

		log.info("Saga 완료");
	}

	@Override
	public void handleImageProcessFailure(String sagaId, ImageProcessedEvent event) {

		MDC.put("errorMsg", event.errorMsg());

		try {
			Saga saga = sagaService.getSaga(sagaId);

			if (saga.getSagaStatus().isTerminal()) {
				log.info("이미 처리된 실패 Saga");

				return;
			}

			switch (event.eventType()) {
				case PUSH -> {
					log.error("이미지 업로드 실패/수동 보상 필요");

					compensationService.compensateProductImageUploadFailure(sagaId, event.referenceCode(),
						event.errorMsg(),
						event.urls());
				}
				case DELETE -> {
					log.error("이미지 삭제 실패");

					sagaService.updateToFail(sagaId, "이미지 삭제 실패 (재시도, DLT 확인) : " + event.errorMsg());
				}
				default -> {
					log.error("미지원 이벤트 실패");

					sagaService.updateToFail(sagaId, "미지원 이벤트 실패: " + event.eventType());
				}
			}
		} catch (DomainException | ServiceException e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("이미지 최종 실패 처리 실패/수동 보상 필요");

			sagaService.updateToFail(sagaId, "이미지 최종 실패 처리 실패/수동 보상 필요: " + event.eventType());
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("이미지 최종 실패 처리 실패/수동 보상 필요 - ExStack: ", e);

			sagaService.updateToFail(sagaId, "이미지 최종 실패 처리 실패/수동 보상 필요: " + event.eventType());
		} finally {
			MDC.clear();
		}
	}
}
