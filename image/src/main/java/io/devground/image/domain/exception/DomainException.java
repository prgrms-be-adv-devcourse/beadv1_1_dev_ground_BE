package io.devground.image.domain.exception;

import io.devground.image.domain.vo.DomainErrorCode;

public class DomainException extends RuntimeException {

	private final DomainErrorCode errorCode;

	public DomainException(DomainErrorCode errorCode) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public DomainException(DomainErrorCode errorCode, Throwable cause) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public DomainErrorCode getErrorCode() {
		return errorCode;
	}
}
