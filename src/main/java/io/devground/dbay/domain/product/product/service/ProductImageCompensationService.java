package io.devground.dbay.domain.product.product.service;

import static io.devground.core.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.common.saga.entity.Saga;
import io.devground.dbay.common.saga.service.SagaService;
import io.devground.dbay.common.saga.vo.SagaStep;
import io.devground.dbay.domain.product.product.client.ImageClient;
import io.devground.dbay.domain.product.product.mapper.ProductMapper;
import io.devground.dbay.domain.product.product.model.entity.Product;
import io.devground.dbay.domain.product.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "saga")
@Service
@RequiredArgsConstructor
public class ProductImageCompensationService {

	private final ImageClient imageClient;
	private final SagaService sagaService;
	private final ProductRepository productRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void compensateProductImageUploadFailure(
		String sagaId, String productCode, String errorMsg, List<String> urls
	) {

		Saga saga = sagaService.getSaga(sagaId);
		SagaStep step = saga.getCurrentStep();

		log.info("이미지 등록 보상 트랜잭션 시작 - SagaId: {}, ProductCode: {}, Step: {}", sagaId, productCode, step);

		if (saga.getSagaStatus().isTerminal()) {
			log.warn("이미 종료된 Saga/업로드 보상 중지 - SagaId: {}, Status: {}, Step: {}", sagaId, saga.getSagaStatus(), step);

			return;
		}

		if (saga.getSagaStatus().isCompensating()) {
			log.warn("이미 보상 중인 Saga/업로드 보상 중지 - SagaId: {}, Status: {}, Step: {}", sagaId, saga.getSagaStatus(), step);

			return;
		}

		sagaService.updateToCompensating(sagaId);

		try {
			Product product = productRepository.findByCode(productCode)
				.orElseThrow(ErrorCode.PRODUCT_NOT_FOUND::throwServiceException);

			String curThumbnailUrl = product.getThumbnailUrl();
			String compensatedThumbnailUrl = "";

			switch (step) {
				case WAITING_S3_UPLOAD ->
					log.info("S3 저장 실패/보상 없이 실패 처리- SagaId: {}, ProductCode: {}", sagaId, productCode);
				case IMAGE_KAFKA_PUBLISHED -> {
					log.info("Kafka 발행 후 실패 보상 - SagaId: {}, ProductCode: {}", sagaId, productCode);

					compensatedThumbnailUrl = imageClient.compensateS3Upload(
							ProductMapper.toDeleteImagesRequest(PRODUCT, productCode, urls))
						.throwIfNotSuccess()
						.data();
				}
				case IMAGE_DB_SAVE -> {
					log.info("이미지 DB 저장 실패 보상 - SagaId: {}, ProductCode: {}", sagaId, productCode);

					compensatedThumbnailUrl = imageClient.compensateDbUpload(
							ProductMapper.toDeleteImagesRequest(PRODUCT, productCode, urls))
						.throwIfNotSuccess()
						.data();
				}
				case PENDING_S3_UPLOAD -> {
					log.info("PresignedURL 발급/보상 불필요 - SagaId: {}, ProductCode: {}", sagaId, productCode);
					compensatedThumbnailUrl = curThumbnailUrl;
				}
				default -> {
					log.error("보상 불가능한 Step - SagaId: {}, ProductCode: {}, Step: {}, ErrorMessage: {}",
						sagaId, productCode, step, errorMsg);

					ErrorCode.INTERNAL_SERVER_ERROR.throwServiceException();
				}
			}

			String validatedThumbnail = this.validateThumbnail(curThumbnailUrl, urls, compensatedThumbnailUrl);
			product.updateThumbnail(validatedThumbnail);

			sagaService.updateToCompensated(
				sagaId,
				String.format("이미지 등록 보상 완료 - SagaId: %s, ProductCode: %s, Exception: %s",
					sagaId, productCode, errorMsg
				)
			);

			log.info("이미지 등록 보상 완료 - SagaId: {}, ProductCode: {}", sagaId, productCode);

		} catch (Exception e) {
			log.error(
				"이미지 등록 보상 트랜잭션 실패 - SagaId: {}, productCode: {}, Exception: {}",
				sagaId, productCode, e.getMessage()
			);

			sagaService.updateToFail(
				sagaId,
				String.format("이미지 등록 보상 트랜잭션 실패 - SagaId: %s, ProductCode: %s, Exception: %s",
					sagaId, productCode, e.getMessage()
				)
			);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void compensateProductImageUpdateFailure(
		String sagaId, String productCode, List<String> deleteUrls, String errorMsg
	) {

		log.error("이미지 업데이트 실패/삭제된 이미지는 복구 불가능 - SagaId: {}, ProductCode: {}, deleteUrls: {}",
			sagaId, productCode, deleteUrls
		);

		Saga saga = sagaService.getSaga(sagaId);
		if (saga.getSagaStatus().isTerminal()) {
			log.error("이미 종료된 Saga/업데이트 보상 중지 - SagaId: {}, Status: {}, Step: {}",
				sagaId, saga.getSagaStatus(), saga.getCurrentStep()
			);

			return;
		}

		if (saga.getSagaStatus().isCompensating()) {
			log.error("이미 보상 중인 Saga/업데이트 보상 중지 - SagaId: {}, Status: {}, Step: {}",
				sagaId, saga.getSagaStatus(), saga.getCurrentStep()
			);

			return;
		}

		sagaService.updateToFail(
			sagaId,
			String.format("이미지 업데이트 실패/수동 복구 필요 - ProductCode: %s, deleteUrls: %s, Exception: %s",
				productCode, deleteUrls, errorMsg)
		);
	}

	private String validateThumbnail(String curThumbnail, List<String> deleteUrls, String compensatedThumbnail) {
		if (ObjectUtils.isEmpty(curThumbnail)) {
			return compensatedThumbnail;
		}

		if (CollectionUtils.isEmpty(deleteUrls)) {
			return curThumbnail;
		}

		boolean isThumbnailDeleted = deleteUrls.contains(curThumbnail);

		if (isThumbnailDeleted) {
			log.info("상품 썸네일 교체 - 기존: {}, 신규: {}", curThumbnail, compensatedThumbnail);

			return compensatedThumbnail;
		}

		log.info("상품 썸네일 유지 - 썸네일: {}", curThumbnail);

		return curThumbnail;
	}
}
