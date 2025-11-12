package io.devground.dbay.domain.saga.vo;

import lombok.Getter;

@Getter
public enum SagaStep {

	INIT,
	PRODUCT_SAVE,
	IMAGE_S3_UPLOAD,
	IMAGE_KAFKA_PUBLISHED,
	IMAGE_DB_SAVE,
	COMPLETE,
	COMPENSATING,
	COMPENSATED,
	FAILED
}
