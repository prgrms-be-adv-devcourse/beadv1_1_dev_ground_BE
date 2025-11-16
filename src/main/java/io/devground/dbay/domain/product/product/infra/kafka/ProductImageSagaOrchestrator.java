package io.devground.dbay.domain.product.product.infra.kafka;

import static io.devground.core.model.vo.ImageType.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.devground.core.event.image.ImageProcessedEvent;
import io.devground.core.event.product.ProductImagesDeleteEvent;
import io.devground.core.event.product.ProductImagesPushEvent;
import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.common.saga.entity.Saga;
import io.devground.dbay.common.saga.service.SagaService;
import io.devground.dbay.common.saga.vo.SagaStep;
import io.devground.dbay.common.saga.vo.SagaType;
import io.devground.dbay.domain.product.product.client.ImageClient;
import io.devground.dbay.domain.product.product.mapper.ProductMapper;
import io.devground.dbay.domain.product.product.service.ProductImageCompensationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: ProductImageSagaOrchestrator 역할 설명
 * 1. 상품 관련 Kafka 모든 단계 흐름 제어
 * 2. 각 단계의 성공/실패 결과 처리
 * 3. 실패 시 보상 트랜잭션 결정 후 실행
 * 4. Saga 상태 관리 총괄
 * 5. 보상 트랜잭션은 새로운 트랜잭션에서 실행되어야 함
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ProductImageSagaOrchestrator {

	private final ImageClient imageClient;
	private final SagaService sagaService;
	private final ProductKafkaProducer productKafkaProducer;
	private final ProductImageCompensationService compensationService;

	public List<URL> startGetPresignedUrlsSaga(String productCode, List<String> imageExtensions) {

		String sagaId = sagaService.startSaga(productCode, SagaType.PRODUCT_IMAGE_REGIST);

		log.info("PresignedUrl 발급 시도 - SagaId: {}, ProductCode: {}", sagaId, productCode);

		try {
			sagaService.updateStep(sagaId, SagaStep.PENDING_S3_UPLOAD);

			BaseResponse<List<URL>> presignedUrlsResponse = imageClient.generatePresignedUrls(
					ProductMapper.toGeneratePresignedRequest(PRODUCT, productCode, imageExtensions)
				)
				.throwIfNotSuccess();

			sagaService.updateStep(sagaId, SagaStep.WAITING_S3_UPLOAD);

			log.info("이미지 PresignedUrl 발급 완료 - SagaId: {}, ProductCode: {}", sagaId, productCode);

			return presignedUrlsResponse.data();
		} catch (Exception e) {
			log.error("이미지 PresignedUrl 발급 실패 - SagaId: {}, ProductCode: {}, Exception: ", sagaId, productCode, e);

			sagaService.updateToFail(sagaId, "이미지 PresignedUrl 발급 실패: " + e.getMessage());

			throw e;
		}
	}

	public void startProductImageUploadSaga(String productCode, List<String> urls) {

		Saga saga = sagaService.findLatestSagaByReferenceCode(productCode);
		String sagaId = saga.getSagaId();

		log.info("이미지 등록 이벤트 진행 - SagaId: {}, ProductCode: {}", sagaId, productCode);

		try {
			sagaService.updateStep(sagaId, SagaStep.IMAGE_KAFKA_PUBLISHED);

			productKafkaProducer.publishProductImagePush(
				new ProductImagesPushEvent(sagaId, PRODUCT, productCode, urls)
			);

			log.info("이미지 등록 이벤트 발행 완료 - SagaId: {}, ProductCode: {}", sagaId, productCode);
		} catch (Exception e) {
			log.error("이미지 등록 이벤트 발행 실패 - SagaId: {}, ProductCode: {}, Exception: ", sagaId, productCode, e);

			compensationService.compensateProductImageUploadFailure(sagaId, productCode, "이벤트 발행 실패: " + e.getMessage());

			throw e;
		}
	}

	public List<URL> startProductImageUpdateSaga(
		String productCode, List<String> deleteUrls, List<String> newExtensions
	) {

		String sagaId = sagaService.startSaga(productCode, SagaType.PRODUCT_IMAGE_UPDATE);

		log.info("상품 이미지 수정 시도 - SagaId: {}, ProductCode: {}", sagaId, productCode);

		try {
			List<URL> updatedPresignedUrls = new ArrayList<>();

			if (!CollectionUtils.isEmpty(deleteUrls) || !CollectionUtils.isEmpty(newExtensions)) {
				updatedPresignedUrls = imageClient.updateImages
						(
							ProductMapper.toUpdateImagesRequest(PRODUCT, productCode, deleteUrls, newExtensions)
						)
					.throwIfNotSuccess()
					.data();
			}

			sagaService.updateStep(sagaId, SagaStep.WAITING_S3_UPLOAD);

			log.info("새로운 이미지 PresignedUrl 발급 완료 - SagaId: {}, ProductCode: {}", sagaId, productCode);

			return updatedPresignedUrls;
		} catch (Exception e) {
			log.error("상품 이미지 수정 실패 - SagaId: {}, ProductCode: {}, Exception: ", sagaId, productCode, e);

			compensationService.compensateProductImageUpdateFailure(sagaId, productCode, deleteUrls, e.getMessage());

			throw e;
		}
	}

	public void startProductImageAllDeleteSaga(String productCode, List<String> deleteUrls) {

		String sagaId = sagaService.startSaga(productCode, SagaType.PRODUCT_IMAGE_DELETE);

		log.info("상품 이미지 삭제 시도 - SagaId: {}, ProductCode: {}", sagaId, productCode);

		try {
			sagaService.updateStep(sagaId, SagaStep.IMAGE_KAFKA_PUBLISHED);

			productKafkaProducer.publishProductImageDelete(
				new ProductImagesDeleteEvent(sagaId, PRODUCT, productCode, deleteUrls)
			);

			log.info("이미지 삭제 이벤트 발행 완료 - SagaId: {}, ProductCode: {}", sagaId, productCode);
		} catch (Exception e) {
			log.error("이미지 삭제 이벤트 발행 실패 - SagaId: {}, ProductCode: {}, Exception: ", sagaId, productCode, e);

			sagaService.updateToFail(sagaId, "이미지 삭제 이벤트 발행 실패" + e.getMessage());

			throw e;
		}
	}

	public void handleImageProcessSuccess(String sagaId, ImageProcessedEvent event) {

		Saga saga = sagaService.getSaga(sagaId);

		if (saga.getSagaStatus().isTerminal()) {
			log.info("이미 처리된 성공 Saga - SagaId: {}, ProductCode: {}", sagaId, event.referenceCode());

			return;
		}

		switch (event.eventType()) {
			case PUSH -> sagaService.updateStep(sagaId, SagaStep.IMAGE_DB_SAVE);
			case DELETE -> sagaService.updateStep(sagaId, SagaStep.IMAGE_DELETED);
		}

		sagaService.updateToSuccess(sagaId);

		log.info("Saga 완료 - SagaId: {}, ProductCode: {}", sagaId, event.referenceCode());
	}

	public void handleImageProcessFailure(String sagaId, ImageProcessedEvent event) {

		Saga saga = sagaService.getSaga(sagaId);

		if (saga.getSagaStatus().isTerminal()) {
			log.info("이미 처리된 실패 Saga - SagaId: {}, ProductCode: {}", sagaId, event.referenceCode());

			return;
		}

		switch (event.eventType()) {
			case PUSH -> {
				log.error("이미지 업로드 실패/수동 보상 필요 - SagaId: {}, ProductCode: {}, ErrorMessage: {}",
					sagaId, event.referenceCode(), event.errorMsg());

				compensationService.compensateProductImageUploadFailure(sagaId, event.referenceCode(), event.errorMsg());
			}
			case DELETE -> {
				log.error("이미지 삭제 실패 - SagaId: {}, ProductCode: {}, ErrorMessage: {}",
					sagaId, event.referenceCode(), event.errorMsg());

				sagaService.updateToFail(sagaId, "이미지 삭제 실패 (재시도, DLT 확인) : " + event.errorMsg());
			}
			default -> {
				log.error("미지원 이벤트 실패 - SagaId: {}, ProductCode: {}, ErrorMessage: {}",
					sagaId, event.referenceCode(), event.errorMsg());

				sagaService.updateToFail(sagaId, "미지원 이벤트 실패: " + event.eventType());
			}
		}
	}
}
