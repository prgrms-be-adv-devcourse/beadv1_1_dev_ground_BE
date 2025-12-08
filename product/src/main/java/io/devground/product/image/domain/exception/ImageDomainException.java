package io.devground.product.image.domain.exception;

import io.devground.product.image.domain.vo.ImageDomainErrorCode;

public class ImageDomainException extends RuntimeException {

	private final ImageDomainErrorCode errorCode;

	public ImageDomainException(ImageDomainErrorCode errorCode) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ImageDomainException(ImageDomainErrorCode errorCode, Throwable cause) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public ImageDomainErrorCode getErrorCode() {
		return errorCode;
	}
}
