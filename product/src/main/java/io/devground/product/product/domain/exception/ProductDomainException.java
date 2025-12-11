package io.devground.product.product.domain.exception;

import io.devground.product.product.domain.vo.ProductDomainErrorCode;

public class ProductDomainException extends RuntimeException {

	private final ProductDomainErrorCode errorCode;

	public ProductDomainException(ProductDomainErrorCode errorCode) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ProductDomainException(ProductDomainErrorCode errorCode, Throwable cause) {
		super(errorCode.getHttpStatus() + " : " + errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public ProductDomainErrorCode getErrorCode() {
		return errorCode;
	}
}
