package io.devground.product.infrastructure.exception;

import io.devground.product.infrastructure.vo.InfraErrorCode;
import lombok.Getter;

@Getter
public class InfraException extends RuntimeException {

	private final InfraErrorCode errorCode;

	public InfraException(InfraErrorCode errorCode) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public InfraException(InfraErrorCode errorCode, Throwable cause) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}
}
