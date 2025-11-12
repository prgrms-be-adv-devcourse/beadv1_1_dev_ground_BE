package io.devground.dbay.domain.saga.vo;

import lombok.Getter;

@Getter
public enum SagaStatus {

	PENDING,
	IN_PROCESS,
	SUCCESS,
	FAILED,
	COMPENSATING
}
