package io.devground.dbay.common.saga.vo;

import lombok.Getter;

@Getter
public enum SagaStatus {

	IN_PROCESS,
	SUCCESS,
	FAILED,
	COMPENSATING,
	COMPENSATED;

	public boolean isTerminal() {
		return this == SUCCESS || this == FAILED || this == COMPENSATING;
	}
}
