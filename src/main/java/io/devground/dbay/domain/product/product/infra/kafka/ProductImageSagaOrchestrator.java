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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: ProductImageSagaOrchestrator 역할 설명
 * 1. 상품 관련 Kafka 모든 단계 흐름 제어
 * 2. 각 단계의 성공/실패 결과 처리
 * 3. 실패 시 보상 트랜잭션 결정 후 실행
 * 4. Saga 상태 관리 총괄
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ProductImageSagaOrchestrator {

	private final ImageClient imageClient;
	private final SagaService sagaService;
	private final ProductKafkaProducer productKafkaProducer;

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

			compensateProductImageUploadFailure(urls, sagaId, productCode, "이벤트 발행 실패: " + e.getMessage());

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

			sagaService.updateToFail(sagaId, "이미지 수정 실패: " + e.getMessage());
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

		try {
			sagaService.isExistSagaById(sagaId);

			switch (event.eventType()) {
				case PUSH -> sagaService.updateStep(sagaId, SagaStep.IMAGE_DB_SAVE);
				case DELETE -> sagaService.updateStep(sagaId, SagaStep.IMAGE_DELETED);
			}

			sagaService.updateToSuccess(sagaId);

			log.info("Saga 완료 - SagaId: {}, ReferenceCode: {}", sagaId, event.referenceCode());
		} catch (Exception e) {
			log.error("Saga 성공 처리 중 오류 발생 - SagaId: {}, Exception: ", sagaId, e);

			throw e;
		}
	}

	public void handleImageProcessFailure(String sagaId, ImageProcessedEvent event) {

		try {
			sagaService.isExistSagaById(sagaId);

			switch (event.eventType()) {
				case PUSH -> compensateProductImageUploadFailure(null, sagaId, event.referenceCode(), event.errorMsg());
				case DELETE -> {
					log.error("이미지 삭제 수동 삭제 필요 - SagaId: {}, ReferenceCode: {}, ErrorMessage: {}",
						sagaId, event.referenceCode(), event.errorMsg());

					sagaService.updateToFail(sagaId, "이미지 삭제 실패 및 수동 삭제 필요: " + event.errorMsg());
				}
			}
		} catch (Exception e) {
			log.error("Saga 실패 처리 중 오류 발생 - SagaId: {}, Exception: ", sagaId, e);

			throw e;
		}
	}

	private void compensateProductImageUploadFailure(
		List<String> urls, String sagaId, String productCode, String errorMsg
	) {

		try {
			log.info("보상 트랜잭션 시작 - SagaId: {}, ProductCode: {}, S3 파일 삭제: {}", productCode, sagaId, urls.size());

			sagaService.updateToCompensating(sagaId);

			productKafkaProducer.publishProductImageDelete(
				new ProductImagesDeleteEvent(sagaId, PRODUCT, productCode, urls)
			);

			log.info("보상 트랜잭션 완료 - SagaId: {}, ProductCode: {}, S3 파일 삭제: {}", sagaId, productCode, urls.size());

			sagaService.updateToFail(
				sagaId,
				String.format("보상 트랜잭션 실행 - SagaId: %s, ProductCode: %s, Exception: %s", sagaId, productCode, errorMsg)
			);
		} catch (Exception e) {
			log.error(
				"보상 트랜잭션 실패 - SagaId: {}, productCode: {}, 삭제 URL: {}, Exception: ", sagaId, productCode, urls, e);

			sagaService.updateToFail(
				sagaId,
				String.format("보상 트랜잭션 실패 - SagaId: %s, ProductCode: %s, Exception: %s",
					sagaId, productCode, e.getMessage()
				)
			);
		}
	}
}
