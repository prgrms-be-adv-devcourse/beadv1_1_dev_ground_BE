package io.devground.product.infrastructure.saga.vo;

import lombok.Getter;

@Getter
public enum SagaStep {

	// common
	INIT,
	COMPLETE,
	FAILED,
	COMPENSATING,
	COMPENSATED,

	// productImageSaga
	IMAGE_KAFKA_PUBLISHED,
	IMAGE_DB_SAVE,
	IMAGE_DELETED
}
