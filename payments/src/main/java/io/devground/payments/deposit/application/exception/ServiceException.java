package io.devground.payments.deposit.application.exception;

import io.devground.payments.deposit.application.exception.vo.ServiceErrorCode;
import lombok.Getter;

@Getter
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

	public ServiceException(ServiceErrorCode errorCode, String additionalInfo) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage() + " - " + additionalInfo);
		this.errorCode = errorCode;
	}
}
