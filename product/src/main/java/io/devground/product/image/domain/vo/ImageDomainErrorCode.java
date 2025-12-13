package io.devground.product.image.domain.vo;

import io.devground.product.image.domain.exception.ImageDomainException;

public enum ImageDomainErrorCode {

	// server
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

	// common
	PAGE_MUST_BE_POSITIVE(400, "페이지 번호는 0보다 커야합니다."),
	PAGE_SIZE_MUST_BE_POSITIVE(400, "페이지 사이즈는 0보다 커야합니다."),

	// image domain validation
	REFERENCE_CODE_MUST_BE_INPUT(400, "이미지 참조 코드는 반드시 입력되어야 합니다."),
	IMAGE_TYPE_MUST_BE_INPUT(400, "이미지 타입은 반드시 입력되어야 합니다."),
	IMAGE_URL_MUST_BE_INPUT(400, "이미지 URL은 반드시 입력되어야 합니다."),
	INVALID_IMAGE_EXTENSION(400, "지원하지 않는 이미지 확장자입니다."),

	// image
	IMAGE_NOT_FOUND(404, "이미지를 찾을 수 없습니다.");

	private final int httpStatus;
	private final String message;

	ImageDomainErrorCode(int httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public ImageDomainException throwException() {
		throw new ImageDomainException(this);
	}

	public ImageDomainException throwException(Throwable cause) {
		throw new ImageDomainException(this, cause);
	}

	public static ImageDomainErrorCode fromHttpStatus(int httpStatus, String msg) {
		for (ImageDomainErrorCode errorCode : ImageDomainErrorCode.values()) {
			if (errorCode.getHttpStatus() == httpStatus && errorCode.getMessage().equals(msg)) {
				return errorCode;
			}
		}

		return INTERNAL_SERVER_ERROR;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public String getMessage() {
		return message;
	}
}