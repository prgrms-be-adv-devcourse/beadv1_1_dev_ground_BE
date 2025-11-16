package io.devground.dbay.domain.product.product.service;

import static io.devground.core.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.common.saga.entity.Saga;
import io.devground.dbay.common.saga.service.SagaService;
import io.devground.dbay.common.saga.vo.SagaStep;
import io.devground.dbay.domain.product.product.client.ImageClient;
import io.devground.dbay.domain.product.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "saga")
@Service
@RequiredArgsConstructor
public class ProductImageCompensationService {

	private final ImageClient imageClient;
	private final SagaService sagaService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void compensateProductImageUploadFailure(
		String sagaId, String productCode, String errorMsg
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
			switch (step) {
				case WAITING_S3_UPLOAD, IMAGE_KAFKA_PUBLISHED -> {
					log.info("S3 저장 실패 보상 - SagaId: {}, ProductCode: {}", sagaId, productCode);

					imageClient.compensateUpload(ProductMapper.toDeleteImagesRequest(PRODUCT, productCode))
						.throwIfNotSuccess();
				}
				case IMAGE_DB_SAVE -> {
					log.info("이미지 DB 저장 실패 보상 - SagaId: {}, ProductCode: {}", sagaId, productCode);

					imageClient.compensateUpload(ProductMapper.toDeleteImagesRequest(PRODUCT, productCode))
						.throwIfNotSuccess();
				}
				case PENDING_S3_UPLOAD ->
					log.info("PresignedURL 발급/보상 불필요 - SagaId: {}, ProductCode: {}", sagaId, productCode);
				default -> {
					log.error("보상 불가능한 Step - SagaId: {}, ProductCode: {}, Step: {}, ErrorMessage: {}",
						sagaId, productCode, step, errorMsg);

					ErrorCode.INTERNAL_SERVER_ERROR.throwServiceException();
				}
			}

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
			log.warn("이미 종료된 Saga/업데이트 보상 중지 - SagaId: {}, Status: {}, Step: {}",
				sagaId, saga.getSagaStatus(), saga.getCurrentStep()
			);

			return;
		}

		if (saga.getSagaStatus().isCompensating()) {
			log.warn("이미 보상 중인 Saga/업데이트 보상 중지 - SagaId: {}, Status: {}, Step: {}",
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
}
