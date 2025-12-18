package io.devground.product.product.infrastructure.adapter.out;

import static io.devground.core.model.vo.ImageType.*;

import java.util.List;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.product.product.domain.exception.ProductDomainException;
import io.devground.product.product.infrastructure.adapter.out.repository.ProductJpaRepository;
import io.devground.product.product.infrastructure.adapter.out.repository.client.ImageClient;
import io.devground.product.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.product.infrastructure.model.web.request.DeleteImagesRequest;
import io.devground.product.product.infrastructure.saga.entity.Saga;
import io.devground.product.product.infrastructure.saga.service.SagaService;
import io.devground.product.product.infrastructure.saga.vo.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "saga")
@Service
@RequiredArgsConstructor
public class ProductImageCompensationService {

	private final ImageClient imageClient;
	private final SagaService sagaService;
	private final ProductJpaRepository productRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void compensateProductImageUploadFailure(
		String sagaId, String productCode, String errorMsg, List<String> urls
	) {

		Saga saga = sagaService.getSaga(sagaId);
		SagaStep step = saga.getCurrentStep();

		log.info("이미지 등록 보상 트랜잭션 시작");

		if (saga.getSagaStatus().isTerminal()) {
			log.warn("이미 종료된 Saga/업로드 보상 중지");

			return;
		}

		if (saga.getSagaStatus().isCompensating()) {
			log.warn("이미 보상 중인 Saga/업로드 보상 중지");

			return;
		}

		sagaService.updateToCompensating(sagaId);

		try {
			ProductEntity product = productRepository.findByCode(productCode)
				.orElseThrow(ErrorCode.PRODUCT_NOT_FOUND::throwServiceException);

			String curThumbnailUrl = product.getThumbnailUrl();
			String compensatedThumbnailUrl = "";

			switch (step) {
				case IMAGE_KAFKA_PUBLISHED -> {
					log.info("Kafka 발행 후 실패 보상");

					compensatedThumbnailUrl = imageClient.compensateS3Upload(
							new DeleteImagesRequest(PRODUCT, productCode, urls)
						)
						.throwIfNotSuccess()
						.data();
				}
				case IMAGE_DB_SAVE -> {
					log.info("이미지 DB 저장 실패 보상");

					compensatedThumbnailUrl = imageClient.compensateDbUpload(
							new DeleteImagesRequest(PRODUCT, productCode, urls))
						.throwIfNotSuccess()
						.data();
				}
				default -> {
					MDC.put("errorMsg", errorMsg);
					log.error("보상 불가능한 Step");

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

			log.info("이미지 등록 보상 완료");

		} catch (ProductDomainException | ServiceException e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("이미지 등록 보상 트랜잭션 실패");

			sagaService.updateToFail(
				sagaId,
				String.format("이미지 등록 보상 트랜잭션 실패 - SagaId: %s, ProductCode: %s, Exception: %s",
					sagaId, productCode, e.getMessage()
				)
			);
		} catch (Exception e) {
			MDC.put("errorMsg", e.getMessage());
			log.error("이미지 등록 보상 트랜잭션 실패 - ExStack: ", e);

			sagaService.updateToFail(
				sagaId,
				String.format("이미지 등록 보상 트랜잭션 실패 - SagaId: %s, ProductCode: %s, Exception: %s",
					sagaId, productCode, e.getMessage()
				)
			);
		} finally {
			MDC.clear();
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void compensateProductImageUpdateFailure(
		String sagaId, String productCode, List<String> deleteUrls, String errorMsg
	) {

		if (!CollectionUtils.isEmpty(deleteUrls)) {
			log.warn("이미지 업데이트 실패/삭제된 이미지는 복구 불가능 - deleteUrls: {}", deleteUrls);
		}

		Saga saga = sagaService.getSaga(sagaId);
		if (saga.getSagaStatus().isTerminal()) {
			log.warn("이미 종료된 Saga/업데이트 보상 중지");

			return;
		}

		if (saga.getSagaStatus().isCompensating()) {
			log.warn("이미 보상 중인 Saga/업데이트 보상 중지");

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

