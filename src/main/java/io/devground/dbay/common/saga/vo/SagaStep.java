package io.devground.dbay.common.saga.vo;

import lombok.Getter;

@Getter
public enum SagaStep {

	// common
	INIT,
	COMPLETE,
	COMPENSATING,

	// productImageSaga
	PENDING_S3_UPLOAD,
	WAITING_S3_UPLOAD,
	IMAGE_KAFKA_PUBLISHED,
	IMAGE_DB_SAVE,
	IMAGE_DELETED,

	// settlementSaga
	SETTLEMENT_COMMAND_SENT,
	DEPOSIT_CHARGE_SUCCESS,
	SETTLEMENT_EVENT_PUBLISHED
}
