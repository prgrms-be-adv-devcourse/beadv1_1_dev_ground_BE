package io.devground.core.model.exception;

import io.devground.core.model.vo.ErrorCode;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
	private final ErrorCode errorCode;

	public ServiceException(ErrorCode errorCode) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ServiceException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}
}
