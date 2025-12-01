package io.devground.product.infrastructure.saga.vo;

import lombok.Getter;

@Getter
public enum SagaStatus {

	IN_PROCESS,
	SUCCESS,
	FAILED,
	COMPENSATING,
	COMPENSATED;

	public boolean isProcess() {
		return this == IN_PROCESS;
	}

	public boolean isTerminal() {
		return this == SUCCESS || this == FAILED || this == COMPENSATED;
	}

	public boolean isCompensating() {
		return this == COMPENSATING;
	}
}
