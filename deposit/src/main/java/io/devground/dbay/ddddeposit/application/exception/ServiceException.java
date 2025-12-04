package io.devground.dbay.ddddeposit.application.exception;

import io.devground.dbay.ddddeposit.application.exception.vo.ServiceErrorCode;

public class ServiceException extends RuntimeException {
	private final ServiceErrorCode errorCode;

	public ServiceException(ServiceErrorCode errorCode) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ServiceException(ServiceErrorCode errorCode, Throwable cause) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}
}
